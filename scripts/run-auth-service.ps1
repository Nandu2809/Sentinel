param(
    [string]$Profile = "default"
)

$ErrorActionPreference = "Stop"
Set-Location -Path "$PSScriptRoot/../backend/auth-service"
mvn spring-boot:run "-Dspring-boot.run.profiles.active=$Profile"

