FROM maven:3.9.9-eclipse-temurin-21-alpine AS build

WORKDIR /workspace
COPY pom.xml pom.xml
COPY common-library/pom.xml common-library/pom.xml
COPY auth-service/pom.xml auth-service/pom.xml
COPY common-library/src common-library/src
COPY auth-service/src auth-service/src
RUN mvn -pl auth-service -am package -DskipTests

FROM eclipse-temurin:21-jre-alpine

WORKDIR /opt/sentinel/auth-service
RUN addgroup -S sentinel && adduser -S sentinel -G sentinel

COPY --from=build /workspace/auth-service/target/auth-service-*.jar app.jar

USER sentinel:sentinel
EXPOSE 8081

ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-XX:+ExitOnOutOfMemoryError", "-jar", "app.jar"]
