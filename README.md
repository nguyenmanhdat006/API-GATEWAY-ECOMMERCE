# API GATEWAY - GITHUB COPILOT GUIDE

**Port:** 8080 | **Files:** 8 | **Time:** 1h

---

## 📁 STRUCTURE (8 files)

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

## 🎯 KEY IMPLEMENTATIONS

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
- Configure base URLs with `services.*-url` properties if needed

---

## ⚙️ FEATURES

✅ **Routing** - Route to correct service
✅ **Direct Service Targeting** - Call backend services by fixed URL
✅ **CORS** - Handle cross-origin requests
✅ **Centralized Auth** - JWT validation
✅ **Logging** - Request/response logging
✅ **Circuit Breaker** - Handle service failures
✅ **Retry** - Auto-retry failed requests

---

## 🧪 TESTING

```bash
# Start services:
# 1. Individual services (8081-8088)
# 2. Gateway (8080)

# Test routing
curl http://localhost:8080/api/products
curl http://localhost:8080/api/cart -H "Authorization: Bearer $TOKEN"

# Check routes
curl http://localhost:8080/actuator/gateway/routes
```

---

## ⏱️ IMPLEMENTATION

```
Phase 1: Main App (10 min)
Phase 2: Config (15 min)
Phase 3: Filters (20 min)
Phase 4: Error Handler (15 min)
```

---

**CRITICAL:** Gateway is the ONLY entry point. Frontend calls port 8080 ONLY.