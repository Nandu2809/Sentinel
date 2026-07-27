param(
    [string]$Profile = "default"
)

$ErrorActionPreference = "Stop"
mvn -pl auth-service -am spring-boot:run "-Dspring-boot.run.profiles=$Profile"

