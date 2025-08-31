FROM eclipse-temurin:21-jre
WORKDIR /app
RUN useradd -m springuser
USER springuser
COPY target/jenkinsDemo-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]