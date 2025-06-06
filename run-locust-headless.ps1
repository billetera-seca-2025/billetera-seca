# Run Locust in headless mode with Docker
$users = Read-Host "Enter number of users (default: 10)"
$spawnRate = Read-Host "Enter spawn rate (default: 1)"
$runTime = Read-Host "Enter run time (e.g., 1m, 5m, 1h) (default: 1m)"
$targetHost = Read-Host "Enter target host (default: http://host.docker.internal:8080)"

# Set default values if empty
if ([string]::IsNullOrEmpty($users)) { $users = "10" }
if ([string]::IsNullOrEmpty($spawnRate)) { $spawnRate = "1" }
if ([string]::IsNullOrEmpty($runTime)) { $runTime = "1m" }
if ([string]::IsNullOrEmpty($targetHost)) { $targetHost = "http://host.docker.internal:8080" }

# Run docker-compose with the specified parameters
docker-compose -f docker-compose.locust.yml run --rm `
    -e LOCUST_USERS=$users `
    -e LOCUST_SPAWN_RATE=$spawnRate `
    -e LOCUST_RUN_TIME=$runTime `
    -e LOCUST_HEADLESS=true `
    -e TARGET_HOST=$targetHost `
    locust 