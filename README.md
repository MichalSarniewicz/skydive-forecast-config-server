# Skydive Forecast - Config Server

[![Java](https://img.shields.io/badge/Java-21-green?logo=openjdk)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.6-brightgreen?logo=springboot)](https://spring.io/projects/spring-boot)
[![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2025.0.0-brightgreen)](https://spring.io/projects/spring-cloud)

Centralized configuration management server for the Skydive Forecast microservices architecture.

## Overview

This service provides externalized configuration for all microservices using Spring Cloud Config Server. Configuration files are stored in a Git repository and served to services on startup.

## Features

- Centralized configuration management
- Git-backed configuration storage
- Environment-specific profiles (dev, test, prod)
- Hot reload support with `/actuator/refresh`
- Version control for configuration changes

## Technology Stack

- **Java**: 21
- **Spring Boot**: 3.5.6
- **Spring Cloud Config Server**: 2025.0.0
- **Configuration Storage**: Git repository (local/remote)
- **Build Tool**: Maven

## Configuration Repository

Configuration files are stored in: `skydive-forecast-svc-config`

### Structure

```
skydive-forecast-svc-config/
├── gateway/
│   ├── gateway.yaml
│   ├── gateway-dev.yaml
│   ├── gateway-prod.yaml
│   └── gateway-test.yaml
├── user-service/
│   ├── user-service.yaml
│   └── user-service-dev.yaml
├── analysis-service/
│   └── ...
├── location-service/
│   └── ...
└── shared/
    └── application-consul.yaml
```

### Naming Convention

- Each service has its own folder
- `{service-name}.yaml` - Default configuration
- `{service-name}-{profile}.yaml` - Profile-specific configuration
- `shared/application-{feature}.yaml` - Shared configuration for all services

## Getting Started

### Prerequisites

- Java 21 or higher
- Maven 3.x
- Git repository with configuration files

### Installation

1. Clone the repository:
```bash
git clone <repository-url>
cd skydive-forecast-config-server
```

2. Build the project:
```bash
mvn clean install
```

3. Run the application:
```bash
mvn spring-boot:run
```

The server will start on port **8888** by default.

## Configuration

### application.yaml

```yaml
server:
  port: 8888

spring:
  application:
    name: config-server
  cloud:
    config:
      server:
        git:
          uri: file://${user.home}/IdeaProjects/skydive-forecast-svc-config
          search-paths: '{application},shared'
          default-label: master
          clone-on-start: true
```

**Note**: `search-paths: '{application},shared'` tells Config Server to:
1. Look in folder matching service name (e.g., `gateway/` for gateway service)
2. Also look in `shared/` folder for common configurations

### Using Remote Git Repository

For production, use a remote Git repository:

```yaml
spring:
  cloud:
    config:
      server:
        git:
          uri: https://github.com/your-org/skydive-forecast-svc-config
          search-paths: '{application},shared'
          username: ${GIT_USERNAME}
          password: ${GIT_PASSWORD}
          default-label: main
```

## API Endpoints

### Get Configuration

```bash
# Get default configuration
curl http://localhost:8888/{service-name}/default

# Get profile-specific configuration
curl http://localhost:8888/{service-name}/dev

# Get specific label/branch
curl http://localhost:8888/{service-name}/dev/master
```

### Examples

```bash
# Gateway dev configuration
curl http://localhost:8888/gateway/dev

# User service prod configuration
curl http://localhost:8888/user-service/prod

# All profiles for analysis service
curl http://localhost:8888/analysis-service/default
```

## Client Configuration

Microservices connect using `bootstrap.yaml`:

```yaml
spring:
  application:
    name: user-service
  cloud:
    config:
      uri: http://config-server:8888
      fail-fast: true
      retry:
        max-attempts: 6
        initial-interval: 1000
  profiles:
    active: dev
```

## Refresh Configuration

Update configuration without restarting services:

1. Update configuration in Git repository
2. Commit changes
3. Trigger refresh:
```bash
curl -X POST http://localhost:8081/actuator/refresh
```

## Docker Deployment

### Dockerfile

```dockerfile
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Docker Compose

```yaml
config-server:
  build: ../skydive-forecast-config-server
  ports:
    - "8888:8888"
  volumes:
    - ../skydive-forecast-svc-config:/config-repo:ro
  environment:
    SPRING_CLOUD_CONFIG_SERVER_GIT_URI: file:///config-repo
```

## Security Considerations

### Production Setup

1. **Encrypt Sensitive Data**:
```bash
# Encrypt password
curl http://localhost:8888/encrypt -d mysecret

# Use in configuration
spring:
  datasource:
    password: '{cipher}AQA...'
```

2. **Secure Config Server**:
```yaml
spring:
  security:
    user:
      name: admin
      password: ${CONFIG_SERVER_PASSWORD}
```

3. **Use HTTPS**:
```yaml
server:
  ssl:
    enabled: true
    key-store: classpath:keystore.p12
    key-store-password: ${KEYSTORE_PASSWORD}
```

## Monitoring

### Health Check

```bash
curl http://localhost:8888/actuator/health
```

### Metrics

```bash
curl http://localhost:8888/actuator/metrics
```

## Integration with Services

All microservices in the Skydive Forecast ecosystem use this Config Server:

- **Gateway** (Port 8080)
- **User Service** (Port 8081)
- **Analysis Service** (Port 8082)
- **Location Service** (Port 8083)

Services automatically fetch configuration on startup based on:
- `spring.application.name`
- `spring.profiles.active`

## Troubleshooting

### Service can't connect to Config Server

```bash
# Check if Config Server is running
curl http://localhost:8888/actuator/health

# Verify configuration exists
curl http://localhost:8888/{service-name}/dev
```

### Configuration not updating

```bash
# Check Git repository
cd skydive-forecast-svc-config
git log

# Verify Config Server sees changes
curl http://localhost:8888/{service-name}/dev
```

### Port already in use

```bash
# Change port in application.yaml
server:
  port: 8889
```

## Development

### Adding New Configuration

1. Create file in config repository:
```bash
cd skydive-forecast-svc-config
touch new-service.yaml
```

2. Add configuration:
```yaml
server:
  port: 8084
spring:
  application:
    name: new-service
```

3. Commit changes:
```bash
git add new-service.yaml
git commit -m "Add new-service configuration"
```

4. Config Server automatically picks up changes

## License

This project is part of the Skydive Forecast system.

## Contact

For questions or support, please contact me.
