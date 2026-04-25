# 🏛️ B3 Matching Engine API

O **Matching Engine** é o núcleo do ecossistema de simulação. Sua responsabilidade é atuar como a própria Bolsa de Valores (B3), recebendo ordens de compra e venda, validando-as contra preços reais de mercado e processando a execução.

## 🚀 Funcionalidades
* **Matching de Ordens**: Executa ou rejeita ordens com base no preço real de mercado (via Redis).
* **Baixa Latência**: Consulta de preços direta em cache (Redis) para decisões rápidas.
* **Persistência Imutável**: Registro de todas as execuções em banco de dados PostgreSQL.
* **Integração Assíncrona**: Comunicação via RabbitMQ com o Broker para feedback de status.

## 🛠️ Stack Tecnológica
* **Java 21** & **Spring Boot 3.3.5**
* **Spring Data JPA** & **PostgreSQL**: Persistência de históricos e auditoria.
* **Spring Data Redis**: Consumo de preços em tempo real injetados pela `market-sync-api`.
* **Spring RabbitMQ**: Mensageria para recebimento de ordens e envio de confirmações.
* **Flyway**: Gerenciamento de migrações de banco de dados.

## 📋 Arquitetura e Fluxo
O serviço opera no centro do fluxo transacional:
1. **Consumo**: Recebe uma ordem da fila `mq-broker-to-b3`.
2. **Preço**: Busca o preço atual do ativo no Redis (prefixo `market:price:`).
3. **Decisão**:
    - **Compra**: Preço Ordem >= Preço Mercado? → `FILLED`
    - **Venda**: Preço Ordem <= Preço Mercado? → `FILLED`
    - Caso contrário → `REJECTED`
4. **Persistência**: Grava o resultado na tabela `order_executions` (PostgreSQL).
5. **Notificação**: Envia o resultado para a exchange `tp-b3-exchange` com destino ao Broker.



## 🔧 Configuração e Variáveis de Ambiente
O serviço depende das seguintes variáveis para funcionar corretamente:

| Variável | Descrição | Exemplo |
| :--- | :--- | :--- |
| `DB_HOST` | Host do PostgreSQL (b3-core-db) | `localhost` |
| `REDIS_HOST` | Host do Redis (market-cache) | `localhost` |
| `RABBITMQ_HOST` | Host do RabbitMQ | `localhost` |

## 🗄️ Banco de Dados (PostgreSQL)
A tabela `order_executions` possui constraints de integridade (`CHECK`) para garantir a consistência dos dados:
- **Status**: Apenas `FILLED`, `REJECTED` ou `EXPIRED`.
- **Side**: Apenas `BUY` ou `SELL`.

## 🐳 Rodando com Docker
```bash
docker build -t b3-matching-engine-api .
```

Certifique-se de que o container esteja na mesma rede (finance-network) que os serviços de infraestrutura.

## 🚦 Health Check
O serviço utiliza o Spring Actuator para monitorar a saúde das conexões:
- Endpoint: GET /actuator/health
- Porta padrão: 8090