package practica1.mockup_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal de la aplicación Mockup API Server.
 * Inicia el contexto de Spring Boot y la configuración del servidor.
 */
@SpringBootApplication
public class MockupApiApplication {

	/**
	 * Método main que sirve como punto de entrada de la aplicación.
	 * 
	 * @param args Argumentos de línea de comandos.
	 */
	public static void main(String[] args) {
		SpringApplication.run(MockupApiApplication.class, args);
	}

}
