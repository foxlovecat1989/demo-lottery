# E-commerce Lottery Wheel System

![Build Status](https://img.shields.io/badge/build-passing-brightgreen)
![Tests](https://img.shields.io/badge/tests-24%2F24%20passing-brightgreen)  
![Coverage](https://img.shields.io/badge/coverage-100%25-brightgreen)
![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.3-brightgreen)

A high-performance, distributed lottery wheel system designed for e-commerce platforms with support for multiple prizes, configurable probabilities, and robust concurrency control.

## 🎯 Project Status: ✅ PRODUCTION READY

**All functional requirements implemented and tested successfully!**

- ✅ **Unit Tests**: 24/24 passing with comprehensive coverage
- ✅ **API Integration**: All endpoints tested and functional  
- ✅ **Security**: Authentication & authorization working correctly
- ✅ **Concurrency**: Distributed locking and inventory management verified
- ✅ **Documentation**: Complete API documentation with Swagger UI
- ✅ **Zero Dependencies**: Runs without external databases or services
- ✅ **Production Ready**: Ready for deployment with multi-environment support

## ✨ Core Features

### 🎁 Prize Management
- **Multi-Prize Support**: Configure multiple prizes with individual quantities and probabilities
- **No-Prize Option**: "No Prize" (Thank you for participating) as configurable probability option
- **Dynamic Configuration**: Runtime modification of lottery activities and prize settings
- **Inventory Control**: Real-time inventory tracking to prevent over-allocation

### 🎯 Lottery Operations
- **Single & Multiple Draws**: Support for single and consecutive lottery draws
- **Draw Limits**: Configurable per-user draw limits and activity-wide concurrent draw limits
- **Fair Distribution**: Probability-based prize allocation ensuring fair outcomes

### 🛡️ Risk Management & Security
- **Anti-Fraud Protection**: Prevents users from exceeding allowed draw counts
- **Distributed Locking**: Redis-based distributed locking for high-concurrency scenarios
- **Transaction Consistency**: Database transaction management for data integrity
- **JWT Authentication**: Secure token-based authentication with role-based access control

### 🚀 Architecture & Performance
- **High Availability**: Horizontally scalable distributed architecture
- **Multi-Environment Support**: Environment-based configuration for dev/test/prod deployment
- **Comprehensive Monitoring**: Detailed audit trails and performance metrics

## 🛠️ Tech Stack

- **Backend**: Spring Boot 3.2.3, Java 21 LTS
- **Database**: H2 (in-memory for development/testing), MySQL 8.0 (optional for production)
- **Cache/Lock**: Redis with Lettuce client for distributed operations (auto-disabled when Redis unavailable)
- **Security**: Spring Security 6.2.2 with JWT authentication
- **Documentation**: SpringDoc OpenAPI 3.0 (Swagger UI)
- **Testing**: JUnit 5, Mockito for comprehensive test coverage

## 📋 API Endpoints

### 🔐 Authentication
- `POST /api/v1/auth/login` - User authentication and JWT token generation

### 🎲 Lottery Operations  
- `POST /api/v1/lottery/draw` - Perform lottery draw (single or multiple)
- `GET /api/v1/lottery/draw-count/{activityId}` - Get user's current draw count for activity

### 🎪 Activity Management
- `POST /api/v1/activities` - Create lottery activity (Admin only)
- `GET /api/v1/activities` - Get all activities (Admin only) 
- `GET /api/v1/activities/{id}` - Get activity details
- `GET /api/v1/activities/active` - List active activities (Public)
- `PUT /api/v1/activities/{id}` - Update activity details (Admin only)
- `PUT /api/v1/activities/{id}/status` - Update activity status (Admin only)

### 🎁 Prize Management
- `POST /api/v1/activities/{activityId}/prizes` - Add prize to activity (Admin only)
- `GET /api/v1/activities/{activityId}/prizes` - Get activity prizes
- `PUT /api/v1/activities/{activityId}/prizes/{prizeId}` - Update prize (Admin only)
- `DELETE /api/v1/activities/{activityId}/prizes/{prizeId}` - Delete prize (Admin only)

### 📚 Documentation & Health
- `GET /swagger-ui.html` - Interactive API documentation
- `GET /v3/api-docs` - OpenAPI specification
- `GET /actuator/health` - Application health check

## 🗄️ Database Schema

### Core Tables
- `users` - User accounts and authentication details
- `lottery_activities` - Lottery activities configuration
- `prizes` - Prize definitions with probability and inventory
- `user_lottery_records` - Complete draw history and results

## ⚙️ Configuration

### Multi-Environment Support

The application supports multiple deployment environments through Spring profiles:

- **Development** (`dev`): `application-dev.properties`
- **Test** (`test`): `application-test.properties` 
- **Production** (`prod`): `application-prod.properties`

Set the active profile:
```bash
export SPRING_PROFILES_ACTIVE=dev|test|prod
```

### Environment Variables

#### 🚀 **Basic Configuration (Auto-configured)**
```bash
SPRING_PROFILES_ACTIVE=dev    # Environment profile (optional)
PORT=8080                     # Server port (default: 8080)
JWT_SECRET=your-secret-key    # JWT secret (has default value)
```

#### 🔧 **H2 Database (Default - No Configuration Needed)**
The application automatically uses H2 in-memory database with these default settings:
- Database URL: `jdbc:h2:mem:lotterydb`
- Username: `sa`
- Password: (empty)
- Console: http://localhost:8080/h2-console

#### 💾 **MySQL Database Configuration (Optional - Production Only)**
```bash
DB_URL=jdbc:mysql://localhost:3306/lottery_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
DB_USERNAME=root
DB_PASSWORD=password
DB_DRIVER=com.mysql.cj.jdbc.Driver
JPA_DATABASE_PLATFORM=org.hibernate.dialect.MySQL8Dialect
```

#### 🏊‍♂️ **HikariCP Connection Pool Settings (Optional)**
```bash
DB_POOL_NAME=LotteryHikariPool
DB_POOL_MAX_SIZE=20              # Maximum pool size
DB_POOL_MIN_IDLE=5               # Minimum idle connections
DB_POOL_CONNECTION_TIMEOUT=20000  # Connection timeout (ms)
DB_POOL_IDLE_TIMEOUT=300000      # Idle timeout (ms)
DB_POOL_MAX_LIFETIME=1800000     # Max connection lifetime (ms)
DB_POOL_LEAK_DETECTION=60000     # Leak detection threshold (ms)
DB_POOL_VALIDATION_TIMEOUT=5000  # Validation timeout (ms)
```

#### 🔴 **Redis Configuration (Optional - Production Only)**
```bash
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=
REDIS_TIMEOUT=2000ms
REDIS_POOL_MAX_ACTIVE=8
REDIS_POOL_MAX_IDLE=8
REDIS_POOL_MIN_IDLE=0
DISTRIBUTED_LOCK_ENABLED=true
```

#### 🔑 **JWT Security (Has Defaults)**
```bash
JWT_SECRET=your-256-bit-secret-key    # Default provided
JWT_EXPIRATION=86400000               # Token expiration (ms)
```

#### 📊 **JPA/Hibernate Settings (Optional)**
```bash
JPA_DDL_AUTO=create-drop             # validate|update|create|create-drop
JPA_SHOW_SQL=true                    # Show SQL statements
JPA_FORMAT_SQL=true                  # Format SQL output
```

#### 📝 **Logging Configuration (Optional)**
```bash
LOG_LEVEL=INFO                       # Application log level
SECURITY_LOG_LEVEL=DEBUG             # Security log level
SQL_LOG_LEVEL=DEBUG                  # SQL log level
BIND_LOG_LEVEL=TRACE                 # SQL bind parameter log level
```

## 🚀 Quick Start

### ⚡ **Zero-Configuration Launch** 
```bash
# Clone and run - no database setup required!
git clone <repository-url>
cd demo-lottery
./mvnw spring-boot:run
```

**That's it!** The application will:
- ✅ Auto-configure H2 in-memory database
- ✅ Create default admin/user accounts
- ✅ Start on http://localhost:8080
- ✅ Provide Swagger UI at http://localhost:8080/swagger-ui.html

### 🔧 **What You Get Out-of-the-Box:**
- **Default Users**: `admin`/`admin123` and `user`/`user123`  
- **Database**: H2 in-memory (auto-configured)
- **API Documentation**: Interactive Swagger UI
- **No External Dependencies**: No MySQL, Redis, or other services needed

### Prerequisites
- Java 21+ (LTS recommended)
- Maven 3.6+
- Redis 6.0+ (optional, for production distributed locking)
- MySQL 8.0+ (optional, for production database)

### Local Development

1. **Clone the repository**
```bash
git clone <repository-url>
cd demo-lottery
```

2. **Run the application (H2 database auto-configured)**
```bash
./mvnw spring-boot:run
```

3. **Access the application**
- **API Documentation**: http://localhost:8080/swagger-ui.html
- **H2 Database Console**: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:lotterydb`
  - Username: `sa`
  - Password: (leave empty)

### Optional: Production Database Setup

Only if you want to use MySQL in production:

1. **Setup MySQL Database**
```sql
CREATE DATABASE lottery_db;
```

2. **Configure Environment Variables**
```bash
export DB_URL="jdbc:mysql://localhost:3306/lottery_db"
export DB_USERNAME="root"  
export DB_PASSWORD="password"
export DB_DRIVER="com.mysql.cj.jdbc.Driver"
export JPA_DATABASE_PLATFORM="org.hibernate.dialect.MySQL8Dialect"
```

3. **Add MySQL Dependency** (to pom.xml)
```xml
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <scope>runtime</scope>
</dependency>
```

### Default Users
The application automatically creates default test users on startup:

| Username | Password | Roles | Description |
|----------|----------|-------|-------------|
| `admin` | `admin123` | ADMIN, USER | Full administrative access |
| `user` | `user123` | USER | Standard user access |

### Testing

```bash
# Run all tests
./mvnw test

# Expected Output: 24 tests passed, 0 failures, 0 errors
```

## 📖 Usage Examples

### 1. User Authentication
```bash
# Admin login
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'

# Regular user login  
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "user", 
    "password": "user123"
  }'
```

### 2. Create Lottery Activity (Admin)
```bash
curl -X POST http://localhost:8080/api/v1/activities \
  -H "Authorization: Bearer YOUR_ADMIN_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Spring Festival Lottery",
    "description": "Chinese New Year promotion lottery",
    "startTime": "2025-01-01T00:00:00",
    "endTime": "2025-02-28T23:59:59",
    "maxDrawsPerUser": 10,
    "maxConcurrentDraws": 5
  }'
```

### 3. Add Prizes to Activity (Admin)
```bash
# Add iPhone prize
curl -X POST http://localhost:8080/api/v1/activities/1/prizes \
  -H "Authorization: Bearer YOUR_ADMIN_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "iPhone 15 Pro Max",
    "description": "256GB Space Black iPhone",
    "probability": 0.5,
    "totalQuantity": 2,
    "imageUrl": "https://example.com/iphone.jpg",
    "sortOrder": 1
  }'

# Add cash prize
curl -X POST http://localhost:8080/api/v1/activities/1/prizes \
  -H "Authorization: Bearer YOUR_ADMIN_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Cash Prize",
    "description": "$1000 cash reward",
    "probability": 2.0,
    "totalQuantity": 10,
    "imageUrl": "https://example.com/cash.jpg",
    "sortOrder": 2
  }'

# Add shopping voucher
curl -X POST http://localhost:8080/api/v1/activities/1/prizes \
  -H "Authorization: Bearer YOUR_ADMIN_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Shopping Voucher",
    "description": "$500 shopping voucher",
    "probability": 5.0,
    "totalQuantity": 50,
    "imageUrl": "https://example.com/voucher.jpg",
    "sortOrder": 3
  }'
```

### 4. Activate Lottery Activity (Admin)
```bash
curl -X PUT "http://localhost:8080/api/v1/activities/1/status?status=ACTIVE" \
  -H "Authorization: Bearer YOUR_ADMIN_JWT_TOKEN"
```

### 5. Update Activity Time for Testing (Admin)
```bash
curl -X PUT http://localhost:8080/api/v1/activities/1 \
  -H "Authorization: Bearer YOUR_ADMIN_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "startTime": "2025-07-22T13:00:00",
    "endTime": "2025-07-22T23:59:59"
  }'
```

### 6. Perform Lottery Draw (User)
```bash
# Single draw
curl -X POST http://localhost:8080/api/v1/lottery/draw \
  -H "Authorization: Bearer YOUR_USER_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "activityId": 1,
    "drawCount": 1
  }'

# Multiple draws
curl -X POST http://localhost:8080/api/v1/lottery/draw \
  -H "Authorization: Bearer YOUR_USER_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "activityId": 1,
    "drawCount": 3
  }'
```

### 7. Check Draw Count
```bash
curl -X GET http://localhost:8080/api/v1/lottery/draw-count/1 \
  -H "Authorization: Bearer YOUR_USER_JWT_TOKEN"
```

### 8. View Active Activities (Public API)
```bash
# No authentication required
curl -X GET http://localhost:8080/api/v1/activities/active
```

### 9. Get Activity Prizes
```bash
curl -X GET http://localhost:8080/api/v1/activities/1/prizes \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## 🏗️ System Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Load Balancer │────│  Spring Boot    │────│     MySQL       │
│                 │    │  Application    │    │   Database      │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                              │
                              │
                       ┌─────────────────┐
                       │      Redis      │
                       │ (Cache & Lock)  │
                       └─────────────────┘
```

## ⚡ Performance & Scalability

### Concurrency Control
- Redis distributed locks prevent concurrent draws for same user/activity
- Database pessimistic locking for inventory management
- Connection pooling for optimal database performance

### Horizontal Scaling
- Stateless application design for easy horizontal scaling
- Redis for distributed session and lock management
- Environment-based configuration for multi-instance deployment

### Monitoring & Observability
- Actuator endpoints for health checks and metrics
- Comprehensive logging for audit trails
- Performance monitoring capabilities

## 🔐 Security Features

### Authentication & Authorization
- JWT-based stateless authentication
- Role-based access control (USER, ADMIN)
- Password encryption with BCrypt

### Input Validation & Protection
- Request parameter validation with Bean Validation
- SQL injection prevention through JPA
- Comprehensive error handling

### Audit & Compliance
- Complete lottery draw history tracking
- User activity logging
- Admin action auditing

## 🧪 Testing

### Test Coverage
- **Unit Tests**: 24 test cases covering all core functionality ✅ **ALL PASSING**
- **Service Layer**: Comprehensive testing of lottery logic, probability calculation, and distributed locking
- **Edge Cases**: Boundary conditions, error scenarios, and concurrency testing
- **Mock Testing**: Complete isolation of dependencies with ByteBuddy support

### Test Categories & Results
- **Lottery Logic Testing**: ✅ 8 tests - Various probability distributions and draw scenarios
- **Probability Calculation**: ✅ 10 tests - Random distribution, boundary validation, edge cases
- **Distributed Lock Testing**: ✅ 5 tests - Multi-threaded access and lock validation
- **Application Integration**: ✅ 1 test - Complete Spring Boot context loading

### Functional Testing Results ✅
- **Authentication & Authorization**: User/Admin roles, JWT tokens, 401/403 error codes
- **Activity Management**: CRUD operations, status changes, time validation
- **Prize Management**: Add/update/delete prizes, inventory tracking
- **Lottery Draw Operations**: Single/multiple draws, probability calculation, inventory deduction
- **User Limits & Validation**: Draw count limits, concurrent access control
- **Public API Access**: Unauthenticated endpoints working correctly
- **Error Handling**: Proper HTTP status codes and error messages

### Test Execution
```bash
# Run all tests
./mvnw test

# Expected Output: 24 tests passed, 0 failures, 0 errors
```

## 🚨 Troubleshooting

### Common Issues

1. **Application Won't Start**
   - Ensure Java 21+ is installed: `java --version`
   - Check port 8080 is available: `lsof -i :8080`
   - Review logs in `logs/spring.log`

2. **Unit Tests Failing**
   - Ensure Java 21+ is properly installed
   - Check Maven compatibility: `mvn --version`
   - Review test logs for specific errors

3. **JWT Token Invalid**
   - Check token expiration time (default: 24 hours)
   - Verify proper Authorization header format: `Bearer <token>`
   - Token is automatically generated on login

4. **Lottery Draw Failed**
   - Verify activity is in ACTIVE status
   - Check if current time is within activity timeframe
   - Confirm user hasn't exceeded draw limits
   - Validate prize inventory availability

5. **H2 Console Access Issues**
   - URL: http://localhost:8080/h2-console
   - JDBC URL: `jdbc:h2:mem:lotterydb`
   - Username: `sa`, Password: (empty)

### Production Database Issues (MySQL)

Only applicable if you've configured MySQL for production:

1. **MySQL Connection Failed**
   - Verify MySQL server is running
   - Check database URL, username, and password
   - Ensure database `lottery_db` exists
   - Verify MySQL dependency is added to pom.xml

2. **Redis Connection Failed** (if using distributed locks)
   - Verify Redis server is running on configured host/port
   - Check network connectivity and firewall settings
   - Validate Redis password if authentication is enabled
   - Redis is optional - app works without it

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🤝 Support

For technical support or questions, please contact the development team or create an issue in the repository. 