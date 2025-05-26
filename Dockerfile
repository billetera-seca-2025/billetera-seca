# Build
FROM gradle:8.5.0-jdk21 AS builder
WORKDIR /app
COPY . /app
RUN gradle clean build -x test

# Run
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
