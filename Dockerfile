FROM eclipse-temurin:17-jdk-slim
WORKDIR /app
COPY server.jar app.jar
EXPOSE 10030
CMD ["java", "-jar", "app.jar"]
