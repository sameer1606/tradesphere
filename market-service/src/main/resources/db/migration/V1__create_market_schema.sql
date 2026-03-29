-- TradeSphere — Market Service V1 Schema

CREATE TABLE IF NOT EXISTS instrument (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    symbol          VARCHAR(30)   NOT NULL,
    name            VARCHAR(255)  NOT NULL,
    exchange        VARCHAR(20)   NOT NULL,
    instrument_type VARCHAR(20)   NOT NULL DEFAULT 'EQ',
    tick_size       NUMERIC(10,5) NOT NULL DEFAULT 0.05,
    lot_size        INT           NOT NULL DEFAULT 1,
    is_active       BOOLEAN       NOT NULL DEFAULT TRUE,
    UNIQUE(symbol, exchange)
);

CREATE INDEX idx_instrument_symbol   ON instrument(symbol);
CREATE INDEX idx_instrument_exchange ON instrument(exchange);

CREATE TABLE IF NOT EXISTS ohlc_candle (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    instrument_id   UUID          NOT NULL REFERENCES instrument(id),
    interval_type   VARCHAR(10)   NOT NULL,
    open_price      NUMERIC(18,4) NOT NULL,
    high_price      NUMERIC(18,4) NOT NULL,
    low_price       NUMERIC(18,4) NOT NULL,
    close_price     NUMERIC(18,4) NOT NULL,
    volume          BIGINT        NOT NULL DEFAULT 0,
    candle_time     TIMESTAMP     NOT NULL,
    UNIQUE(instrument_id, interval_type, candle_time)
);

CREATE INDEX idx_ohlc_instrument_interval ON ohlc_candle(instrument_id, interval_type);
CREATE INDEX idx_ohlc_candle_time         ON ohlc_candle(candle_time);
