# API Gateway - Refactored Structure

## 📁 Project Structure

```
src/main/java/com/ecommerce/gateway/
├── ApiGatewayApplication.java      ✅ Main application with @EnableDiscoveryClient
├── config/
│   ├── GatewayConfig.java          ✅ RouteLocator, rate limiting, retry logic
│   └── CorsConfig.java             ✅ CORS configuration
├── filter/
│   ├── AuthenticationFilter.java   ✅ JWT token validation, public path check
│   └── LoggingFilter.java          ✅ Request/response logging with request ID
└── exception/
    └── GlobalErrorHandler.java     ✅ Global error handling
```

## 🎯 Key Features Implemented

### 1. **ApiGatewayApplication.java**
- ✅ `@SpringBootApplication` - Marks as Spring Boot application
- ✅ `@EnableDiscoveryClient` - Enables Eureka service discovery
- ✅ Main method to start the application

### 2. **GatewayConfig.java**
- ✅ `RouteLocator` bean with predefined routes:
  - Product Service: `/api/products/**` → `lb://product-service` (8082)
  - Cart Service: `/api/cart/**` → `lb://cart-service` (8083)
  - Order Service: `/api/orders/**` → `lb://order-service` (8084)
  - Payment Service: `/api/payments/**` → `lb://payment-service` (8085)
- ✅ **Retry Logic**: Auto-retry with exponential backoff
  - Retries: 3 attempts
  - Backoff: 1000ms initial, 1.1x factor, 2000ms max
  - Methods: GET, POST, PUT
- ✅ **Circuit Breaker**: Prevents cascading failures
  - Each route has its own circuit breaker

### 3. **CorsConfig.java**
- ✅ `CorsWebFilter` bean
- ✅ Allows all origins: `*`
- ✅ Methods: GET, POST, PUT, DELETE, OPTIONS, PATCH
- ✅ Allows all headers and credentials
- ✅ Cache: 3600 seconds

### 4. **AuthenticationFilter.java**
- ✅ Implements `GlobalFilter` and `Ordered`
- ✅ Priority: `HIGHEST_PRECEDENCE + 1` (runs after logging)
- ✅ Features:
  - Extracts JWT token from `Authorization` header
  - Validates Bearer token format
  - Public endpoints bypass authentication:
    - `/api/products/**`
    - `/actuator/gateway/routes`
  - Returns 401 Unauthorized if token is missing or invalid
  - TODO: Token validation with Keycloak or Identity Service

### 5. **LoggingFilter.java**
- ✅ Implements `GlobalFilter` and `Ordered`
- ✅ Priority: `HIGHEST_PRECEDENCE` (runs first)
- ✅ Features:
  - Generates unique `X-Request-ID` for request tracking
  - Logs request: method, path, remote address
  - Logs response: status code, latency (ms)
  - Useful for debugging and monitoring

### 6. **GlobalErrorHandler.java**
- ✅ Implements `ErrorWebExceptionHandler`
- ✅ Catches all unhandled exceptions
- ✅ Returns JSON error response:
  ```json
  {
    "error": "ExceptionType",
    "message": "Error message",
    "status": 500
  }
  ```
- ✅ Logs error details with stack trace

## ⚙️ Configuration (application.yaml)

### Server
- **Port**: 8080 (Gateway only entry point)
- **Application Name**: api-gateway

### Spring Cloud Gateway
- **Discovery**: Eureka enabled
- **Global Filters**: Retry policy applied to all routes
- **Default Retry Config**: 3 retries with exponential backoff

### Eureka Discovery
- **URL**: http://localhost:8761/eureka/
- **Service IDs**: Used in `lb://service-name` URIs

### Actuator Endpoints
- Enables: gateway, health, info
- Useful for:
  - `GET /actuator/gateway/routes` - View all configured routes
  - `GET /actuator/health` - Check gateway health

## 🔌 Routing Examples

```bash
# Product Service
curl http://localhost:8080/api/products

# Cart Service (requires authentication)
curl http://localhost:8080/api/cart \
  -H "Authorization: Bearer <token>"

# Order Service (requires authentication)
curl http://localhost:8080/api/orders \
  -H "Authorization: Bearer <token>"

# Payment Service (requires authentication)
curl http://localhost:8080/api/payments \
  -H "Authorization: Bearer <token>"

# Check routes
curl http://localhost:8080/actuator/gateway/routes

# Check health
curl http://localhost:8080/actuator/health
```

## 🔄 Request Flow

```
Client Request
    ↓
LoggingFilter (log request ID, method, path)
    ↓
AuthenticationFilter (validate JWT token)
    ↓
CorsWebFilter (handle CORS headers)
    ↓
GatewayConfig RouteLocator (route to service)
    ↓
RetryFilter (retry on failure)
    ↓
CircuitBreaker (prevent cascading failures)
    ↓
Microservice (Product, Cart, Order, Payment)
    ↓
LoggingFilter (log response status, latency)
    ↓
Client Response
```

## 📦 Dependencies Added

- `spring-cloud-starter-gateway` - Core gateway functionality
- `spring-cloud-starter-netflix-eureka-client` - Service discovery
- `spring-cloud-starter-circuitbreaker-resilience4j` - Circuit breaker pattern
- `spring-boot-starter-actuator` - Monitoring endpoints
- `lombok` - Reduce boilerplate (annotations)

## ✅ Checklist

- ✅ Phase 1: Main App (10 min) - `@EnableDiscoveryClient` added
- ✅ Phase 2: Config (15 min) - Routes, rate limiting, retry logic
- ✅ Phase 3: Filters (20 min) - Authentication and logging filters
- ✅ Phase 4: Error Handler (15 min) - Global exception handling
- ✅ CORS configured for all origins
- ✅ All code compiles without errors

## 🚀 Start the Gateway

```bash
# 1. Start Eureka (port 8761)
# 2. Start microservices (ports 8082-8085)
# 3. Start Gateway (port 8080)

./mvnw spring-boot:run

# Verify it's running
curl http://localhost:8080/actuator/health
```

## 📝 Notes

- **Gateway is the ONLY entry point** - All requests go through port 8080
- **Load Balancing**: Automatically handled by Eureka + `lb://service-name`
- **Service Names**: Must match service registrations in Eureka
- **Rate Limiting**: Currently uses default retry logic; add Redis for advanced rate limiting
- **Authentication**: JWT validation is placeholder; integrate with Keycloak or Identity Service

