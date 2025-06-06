# Locust Load Testing Guide for BilleteraSeca

This guide will help you run load tests on the BilleteraSeca API using Locust.

## Prerequisites

- Python 3.8 or higher
- PowerShell (Windows)
- Docker and Docker Compose (for containerized version)
- Your Spring Boot application running on `http://localhost:8080` (for local version only)

## Local Execution

1. **Start your Spring Boot application** on port 8080

2. **Run the Locust script**:
   ```powershell
   .\run-locust.ps1
   ```

3. **Open the Locust web interface** in your browser:
   ```
   http://localhost:8089
   ```

## Docker Execution

### Option 1: Web Interface (Recommended for development)

1. **Start all services**:
   ```bash
   docker-compose -f docker-compose.locust.yml up -d
   ```

2. **Open the Locust web interface**:
   ```
   http://localhost:8089
   ```

3. **Configure and run** the tests as described in the "Using the Web Interface" section

4. **To stop**:
   ```bash
   docker-compose -f docker-compose.locust.yml down
   ```

### Option 2: Headless Mode (Recommended for CI/CD)

1. **Run the headless test script**:
   ```powershell
   .\run-locust-headless.ps1
   ```

2. **Enter the parameters** when prompted:
   - Number of users
   - Spawn rate
   - Run time

3. **Results** will be displayed in the console

### Docker Environment Variables

You can configure Locust in Docker using these environment variables:

```yaml
environment:
  - LOCUST_HEADLESS=false  # true for headless mode
  - LOCUST_USERS=10        # number of users
  - LOCUST_SPAWN_RATE=1    # users per second
  - LOCUST_RUN_TIME=1m     # test duration
  - TARGET_HOST=http://host.docker.internal:8080  # target application URL
```

## Using the Locust Web Interface

### Initial Configuration

On the Locust main page (`http://localhost:8089`), configure:

- **Number of users**: Total number of virtual users
  - Recommended to start: 10
  - For intensive tests: 50-100

- **Spawn rate**: Users created per second
  - Recommended to start: 1
  - For intensive tests: 5-10

- **Host**: Configured as `http://localhost:8080` by default

### Starting the Test

1. Click "Start swarming"
2. Wait for the configured number of users to be reached
3. Observe real-time metrics

### Interpreting Results

In the "Statistics" tab you'll see:

- **Type**: Request type (GET/POST)
- **Name**: Endpoint name
- **Requests**: Total number of requests
- **Fails**: Number of failures
- **Median/90%/95%/99%**: Response times in milliseconds
- **Average**: Average response time
- **Min/Max**: Minimum/maximum response time
- **RPS**: Requests per second

### Test Scenarios

The `locustfile.py` includes the following scenarios:

1. **Balance Check** (most frequent)
   - GET `/wallet/balance`
   - Weight: 3 (executes 3 times more than other tasks)

2. **Transaction History**
   - GET `/transactions`
   - Weight: 2

3. **Money Transfer**
   - POST `/wallet/transfer`
   - Weight: 1
   - Random amount between 100 and 1000

4. **Instant Debit**
   - POST `/wallet/instant-debit`
   - Weight: 1
   - Random amount between 1000 and 5000
   - Bank: BBVA

5. **Login**
   - POST `/users/login`
   - Weight: 1

### Stopping the Test

- Click "Stop" in the web interface
- Or press `Ctrl+C` in the terminal

## Tips

1. **Start Soft**:
   - Begin with few users (10)
   - Increase gradually
   - Observe your application's behavior

2. **Monitor**:
   - Check the "Failures" tab for errors
   - Watch response times
   - Verify error rates

3. **Adjust**:
   - If there are many errors, reduce the number of users
   - If everything is fine, increase gradually
   - Adjust task weights as needed

## Troubleshooting

### Docker Issues

1. **Container won't start**:
   - Check logs: `docker-compose -f docker-compose.locust.yml logs locust`
   - Ensure all services are running: `docker-compose -f docker-compose.locust.yml ps`
   - Verify ports are not in use

2. **Can't connect to application**:
   - Verify spring-app is running: `docker-compose ps`
   - Check spring-app logs: `docker-compose logs spring-app`
   - Confirm Docker network is working: `docker network ls`

3. **Permission errors**:
   - Ensure files have correct permissions
   - Run Docker with necessary permissions
   - Check Docker logs: `docker-compose -f docker-compose.locust.yml logs`

### General Issues

1. **Can't connect to application**:
   - Verify your Spring Boot application is running
   - Confirm it's on port 8080
   - Check application logs

2. **Test errors**:
   - Check the "Failures" tab
   - Verify application logs
   - Adjust test parameters

3. **Script won't run**:
   - Ensure Python is installed
   - Run PowerShell as administrator
   - Verify script has execution permissions 