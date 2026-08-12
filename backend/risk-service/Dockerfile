# Stage 1: Build Jar artifact
FROM maven:3.9.8-eclipse-temurin-21-alpine AS builder
WORKDIR /build

COPY pom.xml .
COPY common-library/pom.xml common-library/pom.xml
COPY auth-service/pom.xml auth-service/pom.xml
COPY gateway-service/pom.xml gateway-service/pom.xml
COPY monitoring-service/pom.xml monitoring-service/pom.xml
COPY threat-service/pom.xml threat-service/pom.xml
COPY risk-service/pom.xml risk-service/pom.xml
COPY alert-service/pom.xml alert-service/pom.xml
COPY report-service/pom.xml report-service/pom.xml

RUN mvn dependency:go-offline -B

COPY . .
RUN mvn clean package -pl risk-service -am -DskipTests

# Stage 2: Runtime Minimal Image
FROM eclipse-temurin:21-jre-alpine
RUN addgroup -S sentinel && adduser -S sentinel -G sentinel
WORKDIR /app
COPY --from=builder /build/risk-service/target/*.jar app.jar
RUN chown -R sentinel:sentinel /app
USER sentinel
EXPOSE 8084
HEALTHCHECK --interval=15s --timeout=5s --retries=5 CMD wget -qO- http://localhost:8084/actuator/health/readiness | grep UP || exit 1
ENTRYPOINT ["java", "-XX:+UseG1GC", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
