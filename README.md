# 🏛️ B3 Matching Engine API

The **Matching Engine** is the core of the simulation ecosystem. It acts as the B3 stock exchange itself, receiving buy and sell orders, validating them against real market prices, and processing executions.

> 📘 This service is part of a series of articles documenting the **My Broker B3** ecosystem.
> Follow the full series on [dev.to/rvneto](https://dev.to/rvneto).

---

## 🚀 Features

- **Order Matching**: Executes or rejects orders based on real market prices fetched from Redis.
- **Low Latency**: Direct cache lookup (Redis) for sub-millisecond price decisions.
- **Immutable Persistence**: All executions are recorded in PostgreSQL for auditing.
- **Asynchronous Integration**: Communicates with the Broker via RabbitMQ queues.
- **Dead Letter Queue**: Failed messages are automatically routed to a DLQ for inspection.
- **REST API**: Endpoints for querying execution history, documented via Swagger UI.

---

## 🛠️ Tech Stack

| Technology | Usage |
| :--- | :--- |
| **Java 21** + **Spring Boot 3.5.11** | Core framework |
| **Spring Data JPA** + **PostgreSQL** | Execution persistence and audit |
| **Spring Data Redis** | Real-time price cache lookup |
| **Spring RabbitMQ** | Order intake and result notification |
| **Flyway** | Database schema versioning |
| **SpringDoc OpenAPI** | Swagger UI documentation |

---

## 📋 Architecture & Flow
```
[Broker] ──RabbitMQ──▶ [mq-broker-to-b3] ──▶ [Matching Engine]
                                                       │
                                           Redis: market:price:{TICKER}
                                                       │
                                          ┌────────────┴────────────┐
                                        FILLED                   REJECTED
                                          │                         │
                                      PostgreSQL                 PostgreSQL
                                          │                         │
                                 [mq-b3-to-broker] ◀────────────────┘
                                          │
                                       [Broker]
```

**Matching Rules:**
- **BUY**: order price >= market price → `FILLED`
- **SELL**: order price <= market price → `FILLED`
- Otherwise → `REJECTED`
- **Ticker not found in Redis** → `REJECTED` and broker is notified
- **Processing error** → message routed to `dlq-broker-to-b3`

---

## 🌐 REST API Endpoints

Base URL: `http://localhost:8091/api/v1`

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| GET | `/executions` | List all executions |
| GET | `/executions/order/{orderId}` | Find execution by broker order ID |
| GET | `/executions/ticker/{ticker}` | List executions by stock ticker |
| GET | `/executions/status/{status}` | List executions by status (FILLED, REJECTED, EXPIRED) |

📄 **Swagger UI**: [http://localhost:8091/swagger-ui.html](http://localhost:8091/swagger-ui.html)
📄 **OpenAPI Spec**: [http://localhost:8091/api-docs](http://localhost:8091/api-docs)

---

## 🔧 Environment Variables

| Variable | Description | Default |
| :--- | :--- | :--- |
| `DB_HOST` | PostgreSQL host (b3-core-db) | `localhost` |
| `DB_USER` | PostgreSQL username | `b3_core_user` |
| `DB_PASS` | PostgreSQL password | `b3_core_password` |
| `REDIS_HOST` | Redis host (b3-market-cache) | `localhost` |
| `REDIS_PORT` | Redis port | `6381` |
| `RABBITMQ_HOST` | RabbitMQ host | `localhost` |
| `RABBITMQ_USER` | RabbitMQ username | `admin` |
| `RABBITMQ_PASS` | RabbitMQ password | `admin_pass` |

---

## 🗄️ Database (PostgreSQL)

Table `order_executions` with integrity constraints:
- **status**: only `FILLED`, `REJECTED` or `EXPIRED`
- **side**: only `BUY` or `SELL`
- **execution_time**: auto-populated on insert

---

## 🐳 Running with Docker

```bash
docker build -t b3-matching-engine-api .
```

Make sure the container is on the same network as the infrastructure services:
```bash
docker run --network finance-network \
  -e DB_HOST=b3-core-db \
  -e REDIS_HOST=b3-market-cache \
  -e RABBITMQ_HOST=rabbitmq \
  b3-matching-engine-api
```

## 🚦 Health Check

Spring Actuator is enabled for health monitoring:

- Endpoint: `GET /actuator/health`
- Port: `8091`
