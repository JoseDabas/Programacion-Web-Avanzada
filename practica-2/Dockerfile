FROM eclipse-temurin:17-jdk-jammy AS builder

WORKDIR /home/gradle/src

COPY gradlew .
COPY gradle gradle
COPY app app
COPY settings.gradle .

RUN chmod +x gradlew

RUN ./gradlew :app:shadowJar --no-daemon

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

RUN mkdir /logs
VOLUME /logs

COPY --from=builder /home/gradle/src/app/build/libs/*.jar app.jar

ENV SERVER_PORT=7000

EXPOSE ${SERVER_PORT}

ENTRYPOINT ["java", "-Dorg.slf4j.simpleLogger.logFile=/logs/app.log", "-jar", "app.jar"]
