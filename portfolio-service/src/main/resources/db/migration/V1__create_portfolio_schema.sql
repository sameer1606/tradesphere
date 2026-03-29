-- TradeSphere — Portfolio Service V1 Schema

CREATE TABLE IF NOT EXISTS holding (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID            NOT NULL,
    symbol          VARCHAR(30)     NOT NULL,
    exchange        VARCHAR(20)     NOT NULL,
    product_type    VARCHAR(10)     NOT NULL DEFAULT 'CNC',
    quantity        INT             NOT NULL,
    avg_buy_price   NUMERIC(18,4)   NOT NULL,
    last_price      NUMERIC(18,4),
    unrealised_pnl  NUMERIC(18,4),
    realised_pnl    NUMERIC(18,4)   NOT NULL DEFAULT 0.00,
    day_pnl         NUMERIC(18,4),
    last_updated    TIMESTAMP       NOT NULL DEFAULT NOW(),
    UNIQUE(user_id, symbol, exchange)
);

CREATE INDEX idx_holding_user_id ON holding(user_id);

CREATE TABLE IF NOT EXISTS position (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID            NOT NULL,
    symbol          VARCHAR(30)     NOT NULL,
    exchange        VARCHAR(20)     NOT NULL,
    product_type    VARCHAR(10)     NOT NULL DEFAULT 'MIS',
    buy_qty         INT             NOT NULL DEFAULT 0,
    sell_qty        INT             NOT NULL DEFAULT 0,
    net_qty         INT             NOT NULL DEFAULT 0,
    buy_value       NUMERIC(18,4)   NOT NULL DEFAULT 0.00,
    sell_value      NUMERIC(18,4)   NOT NULL DEFAULT 0.00,
    realised_pnl    NUMERIC(18,4)   NOT NULL DEFAULT 0.00,
    status          VARCHAR(20)     NOT NULL DEFAULT 'OPEN',
    trade_date      DATE            NOT NULL DEFAULT CURRENT_DATE,
    UNIQUE(user_id, symbol, exchange, trade_date)
);

CREATE INDEX idx_position_user_id ON position(user_id);
CREATE INDEX idx_position_date    ON position(trade_date);

CREATE TABLE IF NOT EXISTS pnl_snapshot (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID            NOT NULL,
    total_invested  NUMERIC(18,4)   NOT NULL,
    current_value   NUMERIC(18,4)   NOT NULL,
    total_pnl       NUMERIC(18,4)   NOT NULL,
    day_pnl         NUMERIC(18,4),
    snapshot_at     TIMESTAMP       NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_pnl_snapshot_user_id ON pnl_snapshot(user_id, snapshot_at);
