# Etapa 1 - Construccion
FROM eclipse-temurin:21-jdk-jammy AS builder

WORKDIR /app

# Copiar la configuracion de Gradle
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

#Copiar codigo fuente
COPY src src

RUN sed -i 's/\r$//' gradlew
RUN ./gradlew clean bootJar -x test

# Etapa 2 - Ejecucion
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]