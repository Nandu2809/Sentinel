param(
    [string]$Profile = "default"
)

$ErrorActionPreference = "Stop"
Set-Location -Path "$PSScriptRoot/../backend"
mvn -pl auth-service -am spring-boot:run "-Dspring-boot.run.profiles=$Profile"

