-- =============================================================
-- V2: Account Service Funds Migration
-- =============================================================
-- WHAT CHANGED:
-- V1 stored available_funds + used_margin directly on user_profile.
-- Two mutable columns = no audit trail, concurrency risk, wrong vocabulary.
--
-- WHY:
-- We can't answer "what happened to this user's money?" from two numbers alone.
-- A ledger-based model gives us history, atomicity, and correctness.
--
-- DESIGN:
-- account_profile  → renamed from user_profile, funds columns removed
-- funds_wallet     → current state only (available + blocked), optimistic locking
-- funds_reservation→ one row per open order, tracks what's held and why
-- funds_ledger     → append-only statement, every money movement ever recorded
-- =============================================================

ALTER TABLE user_profile RENAME TO account_profile;
ALTER TABLE account_profile DROP COLUMN available_funds;
ALTER TABLE account_profile DROP COLUMN used_margin;
ALTER TABLE account_profile ADD COLUMN demat_account_number VARCHAR(20);

-- Current funds state. @Version maps to 'version' for optimistic locking.
CREATE TABLE IF NOT EXISTS funds_wallet (
                                            id              UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID          NOT NULL REFERENCES account_profile(id) ON DELETE CASCADE,
    available_funds NUMERIC(18,4) NOT NULL DEFAULT 0.00,
    blocked_funds   NUMERIC(18,4) NOT NULL DEFAULT 0.00,
    version         INT           NOT NULL DEFAULT 0,
    created_at      TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP     NOT NULL DEFAULT NOW()
    );

-- One row per open order. Tracks how much is held and whether it's been released.
CREATE TABLE IF NOT EXISTS funds_reservation (
                                                 id          UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     UUID          NOT NULL REFERENCES account_profile(id) ON DELETE CASCADE,
    order_id    UUID          NOT NULL,
    amount      NUMERIC(18,4) NOT NULL,
    status      VARCHAR(20)   NOT NULL DEFAULT 'ACTIVE',
    created_at  TIMESTAMP     NOT NULL DEFAULT NOW(),
    released_at TIMESTAMP
    );

-- Append-only. Never updated, never deleted. Every money movement lives here.
CREATE TABLE IF NOT EXISTS funds_ledger (
                                            id               UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id          UUID          NOT NULL REFERENCES account_profile(id) ON DELETE CASCADE,
    transaction_type VARCHAR(30)   NOT NULL,
    amount           NUMERIC(18,4) NOT NULL,
    balance_after    NUMERIC(18,4) NOT NULL,
    reference_id     UUID,
    description      VARCHAR(255),
    created_at       TIMESTAMP     NOT NULL DEFAULT NOW()
    );