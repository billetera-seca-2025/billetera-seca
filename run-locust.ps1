# Check if the virtual environment exists
if (-not (Test-Path ".venv")) {
    Write-Host "Creating virtual environment..."
    python -m venv .venv
}

# Activate the virtual environment
Write-Host "Activating virtual environment..."
.\.venv\Scripts\Activate.ps1

# Check if the dependencies are installed
if (-not (Test-Path ".venv\Scripts\locust.exe")) {
    Write-Host "Installing dependencies..."
    pip install -r requirements.txt
}

# Check if the application is running
$response = $null
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/wallet/balance?email=test@test.com" -Method GET -TimeoutSec 1
} catch {
    Write-Host "`n⚠️  WARNING: Could not connect to http://localhost:8080"
    Write-Host "Make sure your Spring Boot application is running on port 8080"
    Write-Host "Press Ctrl+C to cancel or Enter to continue..."
    Read-Host
}

# Run Locust
Write-Host "`n🚀 Starting Locust..."
Write-Host "Open http://localhost:8089 in your browser"
Write-Host "Press Ctrl+C to stop"
python -m locust -f locustfile.py --host=http://localhost:8080 