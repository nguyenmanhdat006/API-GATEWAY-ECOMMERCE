# API GATEWAY

**Port:** 8080 

---

## STRUCTURE

```
src/main/java/com/ecommerce/apigateway/
├── ApiGatewayApplication.java
├── config/
│   └── CorsConfig.java
├── filter/
│   ├── AuthenticationFilter.java
│   └── LoggingFilter.java
└── exception/
    └── GlobalErrorHandler.java
```

---

## KEY IMPLEMENTATIONS

### ApiGatewayApplication.java
```
@SpringBootApplication
Main method
```

### application.yaml
```
- Configure gateway routes
- Configure global filters (retry)
- Configure service URLs
```

### AuthenticationFilter.java (Optional)
```
Implement GlobalFilter
- Extract JWT token from header
- Validate token with Keycloak
- Add user info to headers for downstream services
```

### LoggingFilter.java
```
Implement GlobalFilter, Ordered
- Log request/response
- Add request ID
- Measure latency
```

---

## 🔌 ROUTING

**All requests go through Gateway:**
```
http://localhost:8080/api/products     → Product Service (8082)
http://localhost:8080/api/cart         → Cart Service (8083)
http://localhost:8080/api/orders       → Order Service (8084)
http://localhost:8080/api/payments     → Payment Service (8085)
http://localhost:8080/api/auth         → Auth/User Service (8081)
http://localhost:8080/api/reviews      → Review Service (8086)
http://localhost:8080/api/notifications → Notification Service (8087)
http://localhost:8080/api/shipping     → Shipping Service (8088)
```

**Routing mode:**
- Use service URLs from `application.yaml`
- No service registry required
- Configure base URLs with environment variables via `.env` or `--env-file`

### Environment variables

Copy the example file and adjust URLs as needed:

```bash
cp .env.example .env
```

Run Docker with the env file:

```bash
docker build -t api-gateway:latest .
docker run --env-file .env -p 8080:8080 api-gateway:latest
```

---

##  FEATURES

 **Routing** - Route to correct service
 **Direct Service Targeting** - Call backend services by fixed URL
 **CORS** - Handle cross-origin requests
 **Centralized Auth** - JWT validation
 **Logging** - Request/response logging
 **Circuit Breaker** - Handle service failures
 **Retry** - Auto-retry failed requests

**CRITICAL:** Gateway is the ONLY entry point. Frontend calls port 8080 ONLY.
