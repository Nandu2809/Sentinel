FROM eclipse-temurin:21-jre-alpine
RUN addgroup -S sentinel && adduser -S sentinel -G sentinel
WORKDIR /app
COPY report-service/target/*-SNAPSHOT.jar app.jar
RUN chown -R sentinel:sentinel /app
USER sentinel
EXPOSE 8086
HEALTHCHECK --interval=15s --timeout=5s --retries=5 CMD wget -qO- http://localhost:8086/actuator/health/readiness | grep UP || exit 1
ENTRYPOINT ["java", "-XX:+UseG1GC", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]

