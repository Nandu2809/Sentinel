# Stage 1: Build Jar artifact
FROM maven:3.9.8-eclipse-temurin-21-alpine AS builder
WORKDIR /build
COPY pom.xml .
COPY common-library common-library
COPY alert-service alert-service
RUN mvn clean package -pl common-library,alert-service -am -DskipTests

# Stage 2: Runtime Minimal Image
FROM eclipse-temurin:21-jre-alpine
RUN addgroup -S sentinel && adduser -S sentinel -G sentinel
WORKDIR /app
COPY --from=builder /build/alert-service/target/*.jar app.jar
RUN chown -R sentinel:sentinel /app
USER sentinel
EXPOSE 8085
HEALTHCHECK --interval=15s --timeout=5s --retries=5 CMD wget -qO- http://localhost:8085/actuator/health/readiness | grep UP || exit 1
ENTRYPOINT ["java", "-XX:+UseG1GC", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
