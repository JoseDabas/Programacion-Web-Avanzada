# Etapa 1: Construccion (Build)
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

# Copiamos los archivos de configuracion y el wrapper
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
# Usamos comodin para que si no existe settings.gradle no de error en el COPY
COPY settings.gradl[e] . 

# Permisos de ejecucion requeridos al mover de Windows a contenedor Linux
RUN sed -i 's/\r$//' gradlew
RUN chmod +x ./gradlew

# Copiamos todo el codigo fuente del microservicio
COPY src src

# Construimos el .jar omitiendo tests
RUN ./gradlew bootJar -x test --no-daemon

# Etapa 2: Ejecucion (Production)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copiamos unicamente el JAR compilado desde la etapa "builder"
COPY --from=builder /app/build/libs/*.jar app.jar

# Ejecutamos el JAR
ENTRYPOINT ["java", "-jar", "app.jar"]
