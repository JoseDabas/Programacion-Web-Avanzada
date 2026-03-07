package proyecto;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import java.io.IOException;

import org.bson.types.ObjectId;
import proyecto.clases.Usuario;
import org.mindrot.jbcrypt.BCrypt;
import proyecto.controllers.HomeController;
import proyecto.controllers.URLController;
import proyecto.controllers.UserController;
import proyecto.grpc.UrlServiceImpl;
import proyecto.services.UserServices;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.MongoException;
import io.github.cdimascio.dotenv.Dotenv;
import io.grpc.ServerInterceptors;
import io.grpc.ServerServiceDefinition;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.netty.handler.ssl.ClientAuth;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContext;
import java.io.File;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        // Cargar variables de entorno primero; usar .env solo como fallback en
        // desarrollo
        Dotenv dotenv = Dotenv.configure()
                .directory("app")
                .filename(".env")
                .ignoreIfMalformed()
                .ignoreIfMissing()
                .load();

        String mongoUrl = System.getenv("URL_MONGO");
        if (mongoUrl == null || mongoUrl.isEmpty())
            mongoUrl = dotenv.get("URL_MONGO");
        String dbName = System.getenv("DB_NOMBRE");
        if (dbName == null || dbName.isEmpty())
            dbName = dotenv.get("DB_NOMBRE");
        String jwtSecret = System.getenv("JWT_SECRET");
        if (jwtSecret == null || jwtSecret.isEmpty())
            jwtSecret = dotenv.get("JWT_SECRET");

        if (mongoUrl == null || mongoUrl.isEmpty()) {
            mongoUrl = "mongodb://localhost:27017"; // fallback si falta .env
        }
        if (dbName == null || dbName.isEmpty()) {
            dbName = "proyecto_final";
        }

        System.setProperty("URL_MONGO", mongoUrl);
        System.setProperty("DB_NOMBRE", dbName);
        if (jwtSecret == null || jwtSecret.isEmpty()) {
            jwtSecret = "dev-secret-0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef";
        }
        System.setProperty("JWT_SECRET", jwtSecret);

        // System.setProperty("javax.net.ssl.trustStore",
        // "/etc/ssl/certs/java/cacerts");
        // System.setProperty("javax.net.ssl.trustStorePassword", "changeit");
        // System.setProperty("javax.net.debug", "ssl:handshake");

        // System.setProperty("jdk.tls.client.protocols", "TLSv1.2");

        // Verificar la conexión a MongoDB (sin detener la app si falla)
        try {
            com.mongodb.client.MongoClient mongoClient = MongoClients.create(mongoUrl);
            MongoDatabase database = mongoClient.getDatabase(dbName);
            database.listCollectionNames().first();
            System.out.println("Conexión a MongoDB exitosa!");
        } catch (MongoException e) {
            System.err.println("Error de conexión con MongoDB: " + e.getMessage());
            System.err.println("Aplicando fallback a Mongo local (mongodb://localhost:27017).");
            System.setProperty("URL_MONGO", "mongodb://localhost:27017");
        }

        // Mostrar variables para depuración sin exponer credenciales
        System.out.println("Base de datos: " + System.getProperty("DB_NOMBRE"));

        // Crear el usuario admin si no existe (con contraseña hasheada)
        if (UserServices.getInstance().findByUsername("admin") == null) {
            String hashedAdmin = BCrypt.hashpw("admin", BCrypt.gensalt(12));
            UserServices.getInstance().crear(new Usuario(new ObjectId(), "admin", hashedAdmin, true));
        }

        // Iniciar el servidor gRPC
        new Thread(() -> {
            try {
                String tlsEnabledEnv = System.getenv("GRPC_TLS_ENABLED");
                if (tlsEnabledEnv == null || tlsEnabledEnv.isEmpty())
                    tlsEnabledEnv = dotenv.get("GRPC_TLS_ENABLED");
                boolean tlsEnabled = tlsEnabledEnv != null && tlsEnabledEnv.equalsIgnoreCase("true");
                String serverCertPath = System.getenv("GRPC_SERVER_CERT");
                if (serverCertPath == null || serverCertPath.isEmpty())
                    serverCertPath = dotenv.get("GRPC_SERVER_CERT");
                String serverKeyPath = System.getenv("GRPC_SERVER_KEY");
                if (serverKeyPath == null || serverKeyPath.isEmpty())
                    serverKeyPath = dotenv.get("GRPC_SERVER_KEY");
                String caCertPath = System.getenv("GRPC_CA_CERT");
                if (caCertPath == null || caCertPath.isEmpty())
                    caCertPath = dotenv.get("GRPC_CA_CERT");

                ServerServiceDefinition service = ServerInterceptors.intercept(new UrlServiceImpl(),
                        new proyecto.grpc.GrpcAuthInterceptor());
                Server server;
                if (tlsEnabled && serverCertPath != null && serverKeyPath != null && caCertPath != null
                        && new File(serverCertPath).exists() && new File(serverKeyPath).exists()
                        && new File(caCertPath).exists()) {
                    SslContext sslContext = GrpcSslContexts.forServer(new File(serverCertPath), new File(serverKeyPath))
                            .trustManager(new File(caCertPath))
                            .clientAuth(ClientAuth.REQUIRE)
                            .build();
                    server = NettyServerBuilder.forPort(50051)
                            .sslContext(sslContext)
                            .addService(service)
                            .build();
                } else {
                    server = ServerBuilder.forPort(50051)
                            .addService(service)
                            .build();
                }
                server.start();
                server.awaitTermination();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        // Leemos la variable de entorno de Docker. Si no existe, usamos 7000 por
        // defecto.
        String strPort = System.getenv("SERVER_PORT");
        int port = (strPort != null) ? Integer.parseInt(strPort) : 7000;

        // Iniciar el servidor Javalin
        Javalin app = Javalin.create(config -> {

            // Configuración de archivos estáticos para las plantillas
            config.staticFiles.add(staticFileConfig -> {
                staticFileConfig.hostedPath = "/";
                staticFileConfig.directory = "public/templates";
                staticFileConfig.location = Location.CLASSPATH;
                staticFileConfig.precompress = false;
                staticFileConfig.aliasCheck = null;
            });

            // Configuración de archivos estáticos para la carpeta pública
            config.staticFiles.add(staticFileConfig -> {
                staticFileConfig.hostedPath = "/";
                staticFileConfig.directory = "public";
                staticFileConfig.location = Location.CLASSPATH;
                staticFileConfig.precompress = false;
                staticFileConfig.aliasCheck = null;
            });

        }).start(port);

        // Aplicar rutas
        new HomeController(app).aplicarRutas();
        new UserController(app).aplicarRutas();
        new URLController(app).aplicarRutas();
    }
}
