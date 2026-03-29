# TradeSphere

Mock trading platform. Production-grade Spring Boot microservices for learning.

## Services

| Service              | Port | Database                    |
|----------------------|------|-----------------------------|
| API Gateway          | 8080 | None (stateless)            |
| Identity Service     | 8081 | Postgres + Redis            |
| Account Service      | 8082 | Postgres + Redis            |
| Market Service       | 8083 | Redis + Postgres (candles)  |
| Order Service        | 8084 | Postgres                    |
| Portfolio Service    | 8085 | Postgres + Redis            |
| Notification Service | 8086 | MongoDB                     |

**Kafka topics:** `order.executed` · `order.cancelled` · `order.rejected`
**Redis Pub/Sub:** `price:tick:{symbol}`

## Quick Start

```bash
# 1. Start infrastructure
docker-compose up -d

# 2. Configure
cp .env.example .env
# Fill in FINNHUB_API_KEY, TWELVEDATA_API_KEY, JWT_SECRET

# 3. Build
mvn clean install -DskipTests

# 4. Run services (one terminal each, identity first)
cd identity-service   && mvn spring-boot:run
cd account-service    && mvn spring-boot:run
cd market-service     && mvn spring-boot:run
cd order-service      && mvn spring-boot:run
cd portfolio-service  && mvn spring-boot:run
cd notification-service && mvn spring-boot:run
cd api-gateway        && mvn spring-boot:run
```

## Dev Tools

| Tool            | URL                   |
|-----------------|-----------------------|
| Kafka UI        | http://localhost:9093 |
| Redis Commander | http://localhost:8089 |

## Packages

All code lives under `com.tradesphere.*`

```
com.tradesphere.gateway
com.tradesphere.identity
com.tradesphere.account.{profile,watchlist,funds}
com.tradesphere.market
com.tradesphere.order
com.tradesphere.portfolio
com.tradesphere.notification
```
