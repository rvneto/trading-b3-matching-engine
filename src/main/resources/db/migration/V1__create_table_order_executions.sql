CREATE TABLE order_executions
(
    id             BIGSERIAL PRIMARY KEY,
    order_id       VARCHAR(255)   NOT NULL,
    ticker         VARCHAR(20)    NOT NULL,
    side           VARCHAR(10)    NOT NULL,
    quantity       INTEGER        NOT NULL,
    executed_price NUMERIC(15, 2) NOT NULL,
    status         VARCHAR(20)    NOT NULL,
    execution_time TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Constraint de Check para validar os status permitidos
    CONSTRAINT ck_order_status CHECK (status IN ('FILLED', 'REJECTED', 'EXPIRED')),

    -- Constraint de Check para o lado da operação
    CONSTRAINT ck_order_side CHECK (side IN ('BUY', 'SELL'))
);

-- Índices para performance
CREATE INDEX idx_order_id ON order_executions (order_id);
CREATE INDEX idx_ticker ON order_executions (ticker);