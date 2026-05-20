# Graph Report - API-GATEWAY-ECOMMERCE  (2026-05-20)

## Corpus Check
- 8 files · ~2,575 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 43 nodes · 45 edges · 9 communities (4 shown, 5 thin omitted)
- Extraction: 100% EXTRACTED · 0% INFERRED · 0% AMBIGUOUS
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `b64d6c12`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- [[_COMMUNITY_Community 0|Community 0]]
- [[_COMMUNITY_Community 1|Community 1]]
- [[_COMMUNITY_Community 2|Community 2]]
- [[_COMMUNITY_Community 3|Community 3]]
- [[_COMMUNITY_Community 4|Community 4]]
- [[_COMMUNITY_Community 5|Community 5]]
- [[_COMMUNITY_Community 6|Community 6]]
- [[_COMMUNITY_Community 7|Community 7]]
- [[_COMMUNITY_Community 8|Community 8]]

## God Nodes (most connected - your core abstractions)
1. `AuthenticationFilter` - 7 edges
2. `LoggingFilter` - 5 edges
3. `API GATEWAY` - 5 edges
4. `KEY IMPLEMENTATIONS` - 5 edges
5. `GlobalErrorHandler` - 3 edges
6. `🔌 ROUTING` - 3 edges
7. `Environment variables` - 3 edges
8. `ApiGatewayApplication` - 2 edges
9. `CorsConfig` - 2 edges
10. `STRUCTURE` - 2 edges

## Surprising Connections (you probably didn't know these)
- `AuthenticationFilter` --implements--> `GlobalFilter`  [EXTRACTED]
  src/main/java/com/ecommerce/gateway/filter/AuthenticationFilter.java →   _Bridges community 2 → community 1_

## Communities (9 total, 5 thin omitted)

### Community 0 - "Community 0"
Cohesion: 0.22
Nodes (9): ApiGatewayApplication.java, application.yaml, AuthenticationFilter.java (Optional), code:block2 (@SpringBootApplication), code:block3 (- Configure gateway routes), code:block4 (Implement GlobalFilter), code:block5 (Implement GlobalFilter, Ordered), KEY IMPLEMENTATIONS (+1 more)

### Community 1 - "Community 1"
Cohesion: 0.43
Nodes (3): LoggingFilter, GlobalFilter, Ordered

### Community 3 - "Community 3"
Cohesion: 0.4
Nodes (4): API GATEWAY, code:block1 (src/main/java/com/ecommerce/apigateway/), FEATURES, STRUCTURE

### Community 4 - "Community 4"
Cohesion: 0.4
Nodes (5): code:block6 (http://localhost:8080/api/products     → Product Service (80), code:bash (cp .env.example .env), code:bash (docker build -t api-gateway:latest .), Environment variables, 🔌 ROUTING

## Knowledge Gaps
- **10 isolated node(s):** `AppGatewayProperties`, `code:block1 (src/main/java/com/ecommerce/apigateway/)`, `code:block2 (@SpringBootApplication)`, `code:block3 (- Configure gateway routes)`, `code:block4 (Implement GlobalFilter)` (+5 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **5 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `API GATEWAY` connect `Community 3` to `Community 0`, `Community 4`?**
  _High betweenness centrality (0.123) - this node is a cross-community bridge._
- **Why does `KEY IMPLEMENTATIONS` connect `Community 0` to `Community 3`?**
  _High betweenness centrality (0.121) - this node is a cross-community bridge._
- **Why does `🔌 ROUTING` connect `Community 4` to `Community 3`?**
  _High betweenness centrality (0.069) - this node is a cross-community bridge._
- **What connects `AppGatewayProperties`, `code:block1 (src/main/java/com/ecommerce/apigateway/)`, `code:block2 (@SpringBootApplication)` to the rest of the system?**
  _10 weakly-connected nodes found - possible documentation gaps or missing edges._