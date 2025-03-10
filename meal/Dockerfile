# Use a lightweight JDK image
FROM openjdk:23-jdk-slim

# Set the working directory inside the container
WORKDIR /app
COPY build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]

# Expose the default Spring Boot port
EXPOSE 8080
