# Stage 1: Compile and Build
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copy the architectural blueprint files
COPY pom.xml .
COPY task-service ./task-service
COPY notification-service ./notification-service

# Build only the task-service module along with its parent dependencies
RUN mvn -pl task-service -am clean package -DskipTests

# Stage 2: Ultra-lightweight Runtime Environment
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy only the compiled binary executable from Stage 1
COPY --from=build /app/task-service/target/*.jar app.jar

EXPOSE 8080

# Run with optimized container memory allocations
ENTRYPOINT ["java", "-jar", "app.jar"]