-- TradeSphere — Order Service V1 Schema

CREATE TYPE order_status  AS ENUM ('PENDING','OPEN','PARTIAL','EXECUTED','CANCELLED','REJECTED');
CREATE TYPE order_type    AS ENUM ('MARKET','LIMIT','SL','SL_M');
CREATE TYPE product_type  AS ENUM ('CNC','MIS');
CREATE TYPE order_side    AS ENUM ('BUY','SELL');

CREATE TABLE IF NOT EXISTS orders (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id             UUID            NOT NULL,
    symbol              VARCHAR(30)     NOT NULL,
    exchange            VARCHAR(20)     NOT NULL,
    order_type          order_type      NOT NULL,
    product_type        product_type    NOT NULL,
    side                order_side      NOT NULL,
    quantity            INT             NOT NULL,
    filled_qty          INT             NOT NULL DEFAULT 0,
    limit_price         NUMERIC(18,4),
    trigger_price       NUMERIC(18,4),
    avg_exec_price      NUMERIC(18,4),
    status              order_status    NOT NULL DEFAULT 'PENDING',
    rejection_reason    VARCHAR(500),
    idempotency_key     VARCHAR(100)    UNIQUE,
    placed_at           TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP
);

CREATE INDEX idx_orders_user_id ON orders(user_id);
CREATE INDEX idx_orders_status  ON orders(status);
CREATE INDEX idx_orders_symbol  ON orders(symbol, status);

CREATE TABLE IF NOT EXISTS trade (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id        UUID            NOT NULL REFERENCES orders(id),
    user_id         UUID            NOT NULL,
    symbol          VARCHAR(30)     NOT NULL,
    side            order_side      NOT NULL,
    quantity        INT             NOT NULL,
    price           NUMERIC(18,4)   NOT NULL,
    brokerage       NUMERIC(18,4)   NOT NULL DEFAULT 0.00,
    executed_at     TIMESTAMP       NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_trade_order_id ON trade(order_id);
CREATE INDEX idx_trade_user_id  ON trade(user_id);

CREATE TABLE IF NOT EXISTS outbox_event (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    aggregate_id    UUID            NOT NULL,
    event_type      VARCHAR(100)    NOT NULL,
    payload         TEXT            NOT NULL,
    published       BOOLEAN         NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_outbox_unpublished ON outbox_event(published, created_at) WHERE published = FALSE;
