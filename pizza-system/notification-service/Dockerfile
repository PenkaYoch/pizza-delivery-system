# Stage 1: Compile and Build
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
COPY task-service ./task-service
COPY notification-service ./notification-service

# Build only the notification-service module
RUN mvn -pl notification-service -am clean package -DskipTests

# Stage 2: Ultra-lightweight Runtime Environment
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=build /app/notification-service/target/*.jar app.jar

ENV SPRING_PROFILES_ACTIVE=docker
ENV SPRING_MONGODB_URI=mongodb://mongodb:27017/notification_db
ENV SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:29094

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]