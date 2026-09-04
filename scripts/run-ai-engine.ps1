$ErrorActionPreference = "Stop"
$AiEngineDir = Resolve-Path "$PSScriptRoot/../ai-engine"
Set-Location -Path $AiEngineDir

$VenvPython = Join-Path $AiEngineDir ".venv\Scripts\python.exe"

if (-not (Test-Path $VenvPython)) {
    Write-Host "Virtual environment not found. Creating Python 3.12 .venv..." -ForegroundColor Yellow
    py -3.12 -m venv .venv
    & $VenvPython -m pip install torch==2.2.0 --extra-index-url https://download.pytorch.org/whl/cpu
    & $VenvPython -m pip install -r requirements.txt
    & $VenvPython -m pip install "numpy<2.0" "scipy<1.15.0"
}

$env:PYTHONPATH = "anomaly-detection"
Write-Host "Starting Sentinel AI Engine on http://localhost:8000 using Python 3.12 (.venv)..." -ForegroundColor Green
& $VenvPython -m app.main
