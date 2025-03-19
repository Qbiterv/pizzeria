# Use a lightweight JDK image
FROM openjdk:23-jdk-slim

# Set the working directory inside the container
WORKDIR /app

RUN mkdir -p /usr/local/newrelic
COPY ./newrelic /usr/local/newrelic/
ADD ./mail/newrelic.yml /usr/local/newrelic/newrelic.yml

# Copy the built JAR file into the container
COPY mail/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-javaagent:/usr/local/newrelic/newrelic.jar", "-jar", "app.jar"]

# Expose the default Spring Boot port
EXPOSE 8090