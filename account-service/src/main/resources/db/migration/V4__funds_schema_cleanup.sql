-- V4: Schema quality fixes for funds tables
-- Rename misleading user_id FKs to account_profile_id
-- Add uniqueness, indexes, and check constraints

ALTER TABLE funds_wallet RENAME COLUMN user_id TO account_profile_id;
ALTER TABLE funds_reservation RENAME COLUMN user_id TO account_profile_id;
ALTER TABLE funds_ledger RENAME COLUMN user_id TO account_profile_id;

-- One wallet per account profile
CREATE UNIQUE INDEX uq_funds_wallet_account_profile_id
    ON funds_wallet(account_profile_id);

-- One reservation per order
CREATE UNIQUE INDEX uq_funds_reservation_order_id
    ON funds_reservation(order_id);

-- Lookup active reservations by account
CREATE INDEX idx_funds_reservation_account_status
    ON funds_reservation(account_profile_id, status);

-- Lookup ledger history by account chronologically
CREATE INDEX idx_funds_ledger_account_created
    ON funds_ledger(account_profile_id, created_at);

-- Enforce valid status values
ALTER TABLE funds_reservation
    ADD CONSTRAINT chk_funds_reservation_status
        CHECK (status IN ('ACTIVE', 'RELEASED', 'SETTLED'));

-- Enforce valid transaction types
ALTER TABLE funds_ledger
    ADD CONSTRAINT chk_funds_ledger_transaction_type
        CHECK (transaction_type IN ('CREDIT', 'DEBIT', 'BLOCK', 'UNBLOCK', 'SETTLE'));