FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY target/*.jar app.jar
# Instalação do curl para o healthcheck do Docker
RUN apk add --no-cache curl
ENTRYPOINT ["java", "-jar", "app.jar"]