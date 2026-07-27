FROM maven:3.9.9-eclipse-temurin-21-alpine AS build

WORKDIR /workspace
COPY pom.xml pom.xml
COPY common-library/pom.xml common-library/pom.xml
COPY gateway-service/pom.xml gateway-service/pom.xml
COPY common-library/src common-library/src
COPY gateway-service/src gateway-service/src
RUN mvn -pl gateway-service -am package -DskipTests

FROM eclipse-temurin:21-jre-alpine

WORKDIR /opt/sentinel/gateway-service
RUN addgroup -S sentinel && adduser -S sentinel -G sentinel

COPY --from=build /workspace/gateway-service/target/gateway-service-*.jar app.jar

USER sentinel:sentinel
EXPOSE 8080

ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-XX:+ExitOnOutOfMemoryError", "-jar", "app.jar"]
