# Use a lightweight JDK image
FROM openjdk:23-jdk-slim

RUN mkdir -p /usr/local/newrelic
COPY ./newrelic /usr/local/newrelic/
ADD ./meal/newrelic.yml /usr/local/newrelic/newrelic.yml

# Copy the built JAR file into the container
COPY meal/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-javaagent:/usr/local/newrelic/newrelic.jar", "-jar", "app.jar"]

# Expose the default Spring Boot port
EXPOSE 9010
