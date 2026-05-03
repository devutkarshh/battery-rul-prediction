# start_all.ps1 — Launch DB init, AI service, backend, and open browser
# Usage: Run from project root in PowerShell:
#   Set-ExecutionPolicy -Scope Process -ExecutionPolicy RemoteSigned; .\start_all.ps1

$projectRoot = Split-Path -Parent $MyInvocation.MyCommand.Definition
Write-Host "Project root: $projectRoot"

# 1) Ensure Python venv exists; create if missing
$venvPath = Join-Path $projectRoot ".venv"
if (-Not (Test-Path $venvPath)) {
    Write-Host "Creating Python venv..."
    python -m venv $venvPath
}

# 2) Install Python requirements for ai-service
Write-Host "Installing Python dependencies for ai-service..."
Push-Location (Join-Path $projectRoot "ai-service")
& $venvPath\Scripts\Activate.ps1
if (Test-Path "requirements.txt") {
    pip install -r requirements.txt
}
Pop-Location

# 3) Run DB init script (init_db.py)
Write-Host "Initializing database (init_db.py)..."
Push-Location $projectRoot
& $venvPath\Scripts\Activate.ps1
python .\init_db.py
Pop-Location

# 4) Start AI service in a new PowerShell window
Write-Host "Starting AI service (uvicorn) in a new window..."
$aiCommand = "cd `"$projectRoot\ai-service`"; & `"$venvPath\Scripts\Activate.ps1`"; uvicorn main:app --host 0.0.0.0 --port 5000 --reload"
Start-Process powershell -ArgumentList "-NoExit","-Command","$aiCommand"

# 5) Start backend (Maven) in a new PowerShell window
Write-Host "Starting backend (Spring Boot via Maven) in a new window..."
$backendCommand = "cd `"$projectRoot\backend`"; mvn spring-boot:run"
Start-Process powershell -ArgumentList "-NoExit","-Command","$backendCommand"

# 6) Open browser to site
Start-Sleep -Seconds 4
Write-Host "Opening browser to http://localhost:8080"
Start-Process "http://localhost:8080"

Write-Host "All processes launched. Check the two new PowerShell windows for logs."