FROM openjdk:17-ea-11-jdk-slim
VOLUME /tmp
COPY ./catalog-service-mariadb-1.0.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]