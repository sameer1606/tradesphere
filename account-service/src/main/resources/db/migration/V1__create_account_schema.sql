-- TradeSphere — Account Service V1 Schema

CREATE TABLE IF NOT EXISTS user_profile (
    id               UUID PRIMARY KEY,
    full_name        VARCHAR(255)  NOT NULL,
    phone            VARCHAR(20)   UNIQUE,
    pan_number       VARCHAR(20)   UNIQUE,
    kyc_status       VARCHAR(50)   NOT NULL DEFAULT 'PENDING',
    available_funds  NUMERIC(18,2) NOT NULL DEFAULT 0.00,
    used_margin      NUMERIC(18,2) NOT NULL DEFAULT 0.00,
    created_at       TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMP
);

CREATE TABLE IF NOT EXISTS watchlist (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     UUID         NOT NULL REFERENCES user_profile(id) ON DELETE CASCADE,
    name        VARCHAR(100) NOT NULL,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    UNIQUE(user_id, name)
);

CREATE TABLE IF NOT EXISTS watchlist_item (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    watchlist_id    UUID         NOT NULL REFERENCES watchlist(id) ON DELETE CASCADE,
    symbol          VARCHAR(30)  NOT NULL,
    exchange        VARCHAR(10)  NOT NULL,
    display_order   INT          NOT NULL DEFAULT 0,
    UNIQUE(watchlist_id, symbol)
);

CREATE INDEX idx_watchlist_user_id      ON watchlist(user_id);
CREATE INDEX idx_watchlist_item_list_id ON watchlist_item(watchlist_id);
