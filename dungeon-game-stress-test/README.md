# Dungeon Game Stress Test

A Spring Boot application that implements the classic "Dungeon Game" algorithm with comprehensive stress testing capabilities using Gatling.

The Dungeon Game calculates the minimum initial health points needed for a knight to successfully traverse a dungeon and rescue a princess. The application includes:

- RESTful API for dungeon calculations
- PostgreSQL database for storing results
- Comprehensive unit and integration tests
- Gatling stress tests for performance analysis
- Docker containerization
- Automatic container runtime detection (Docker/Podman)

## 🛠 Prerequisites

Choose one of the following container runtimes:
- **Docker** (with Docker Compose v2+) or **Docker Desktop**
- **Podman** (with Podman Compose)

Java requirements:
- **JDK 23** (for local development)
- **Maven** (included via wrapper)

## 🚀 Start Using Scripts (Recommended)

1. **Start everything (database + application):**
   ```bash
   ./run.sh
   ```

2. **Run stress tests:**
   ```bash
   ./stress.sh
   ```

3. **Stop everything:**
   ```bash
   ./stop.sh
   ```

## 🧪 Running Stress Tests

The application includes comprehensive stress tests using Gatling.

### Available Stress Tests

1. **DungeonGameLoadTest** - Standard load testing
2. **DungeonGameStressTest** - High-volume stress testing

### Running Stress Tests
```bash
./stress.sh
```

#### Method 2: Manual Maven Command
```bash
# Run all stress tests
./mvnw gatling:test

# Run specific test
./mvnw gatling:test -Dgatling.simulationClass=com.codegik.stress.DungeonGameStressTest
```

### Stress Test Configuration

The stress tests simulate:
- **Load Test**: 50 users over 30 seconds
- **Stress Test**: 100 users over 60 seconds
- Various dungeon sizes (2x2, 3x3, 5x5, 10x10)
- Mixed read/write operations

### Viewing Stress Test Results

After running stress tests, results are available in:
```
target/gatling/
├── dungeongamestresstest-[timestamp]/
│   ├── index.html          # Main report
│   ├── simulation.log      # Raw data
│   └── js/                 # Report assets
```

Open `target/gatling/[test-run]/index.html` in your browser to view detailed performance reports.

## 🔗 API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/dungeon/health` | Health check |
| POST | `/api/dungeon/calculate` | Calculate dungeon result |
| GET | `/api/dungeon/results` | Get all results |
| GET | `/api/dungeon/results/dimensions/{rows}/{cols}` | Get results by dimensions |
| GET | `/api/dungeon/stats/count` | Get total count |

### Example API Usage

```bash
# Health check
curl -X GET http://localhost:8080/api/dungeon/health

# Calculate dungeon
curl -X POST http://localhost:8080/api/dungeon/calculate \
  -H "Content-Type: application/json" \
  -d '{"dungeon": [[-3, 5], [1, -4]]}'

# Get all results
curl -X GET http://localhost:8080/api/dungeon/results
```

## 🧪 Testing

### Unit and Integration Tests

```bash
# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=DungeonGameControllerTest

# Run with coverage
./mvnw test jacoco:report
```

### Test Categories

1. **Unit Tests** (`DungeonGameTest`) - Algorithm testing
2. **Integration Tests** (`DungeonGameControllerTest`) - API testing with HttpClient
3. **Stress Tests** (`stress/`) - Performance testing with Gatling

### Large Array Stress Test

The integration tests include a 50x50 dungeon test that validates:
- Algorithm performance with large datasets
- Database operations under load
- API response times

## 🐳 Docker Usage

### Architecture

The Docker setup includes:
- **PostgreSQL 15 Alpine** - Database service
- **Spring Boot App** - Application service (built with JDK 23)

### Services

| Service | Port | Description |
|---------|------|-------------|
| `dungeon-app` | 8080 | Spring Boot application |
| `postgres` | 5432 | PostgreSQL database |

### Docker Commands

```bash
# Build and start
docker compose up -d --build

# View logs
docker compose logs -f dungeon-app
docker compose logs -f postgres

# Stop services
docker compose down

# Remove volumes (clean slate)
docker compose down -v
```

## ⚙️ Manual Setup

If you prefer to run components separately:

### 1. Start PostgreSQL
```bash
docker run -d \
  --name dungeon-postgres \
  -e POSTGRES_DB=dungeon_game \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=password \
  -p 5432:5432 \
  postgres:15-alpine
```

### 2. Build Application
```bash
./mvnw clean package -DskipTests
```

### 3. Run Application
```bash
java -jar target/dungeon-game-stress-test-0.0.1-SNAPSHOT.jar
```

## 📁 Project Structure

```
├── src/
│   ├── main/java/com/codegik/
│   │   ├── App.java                    # Main application class
│   │   ├── controller/                 # REST controllers
│   │   ├── dto/                        # Data transfer objects
│   │   ├── entity/                     # JPA entities
│   │   ├── game/                       # Core algorithm
│   │   ├── repository/                 # Data repositories
│   │   └── service/                    # Business logic
│   ├── main/resources/
│   │   ├── application.properties      # App configuration
│   │   └── schema.sql                  # Database schema
│   └── test/
│       ├── java/com/codegik/
│       │   ├── controller/             # Integration tests
│       │   ├── game/                   # Unit tests
│       │   └── stress/                 # Gatling stress tests
│       └── resources/
│           ├── application.properties  # Test configuration
│           └── schema.sql              # Test schema (H2)
├── docker-compose.yml                  # Container orchestration
├── Dockerfile                          # App container definition
├── run.sh                              # Start script
├── stop.sh                             # Stop script
├── stress.sh                           # Stress test script
└── test-api.sh                         # API test script
```

## 🔧 Configuration

### Application Properties

**Production** (`src/main/resources/application.properties`):
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/dungeon_game
spring.datasource.username=postgres
spring.datasource.password=password
server.port=8080
```

**Test** (`src/test/resources/application.properties`):
```properties
spring.datasource.url=jdbc:h2:mem:testdb;MODE=PostgreSQL;CASE_INSENSITIVE_IDENTIFIERS=TRUE
spring.datasource.driver-class-name=org.h2.Driver
```

## 📊 Performance Metrics

The stress tests measure:
- **Response times** (min, max, mean, percentiles)
- **Throughput** (requests per second)
- **Error rates**
- **Database performance** under load
- **Memory usage** patterns

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Add tests for new functionality
4. Run the test suite: `./mvnw test`
5. Run stress tests: `./stress.sh`
6. Submit a pull request

## 📄 License

This project is for educational and stress testing purposes.

---

**Happy Testing! 🎯**
