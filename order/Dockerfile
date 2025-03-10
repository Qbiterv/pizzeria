# Use a lightweight JDK image
FROM openjdk:23-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the built JAR file into the container
ARG MODULE_NAME
COPY build/libs/${MODULE_NAME}.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]


# Expose the default Spring Boot port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
