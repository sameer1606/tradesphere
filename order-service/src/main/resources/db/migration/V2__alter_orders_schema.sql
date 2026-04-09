-- Drop trade table (rebuilding properly later)
DROP TABLE IF EXISTS trade;

-- orders: rename columns
ALTER TABLE orders RENAME COLUMN id TO order_id;
ALTER TABLE orders RENAME COLUMN user_id TO account_profile_id;
ALTER TABLE orders RENAME COLUMN limit_price TO price;
ALTER TABLE orders RENAME COLUMN placed_at TO created_at;

-- orders: fix status enum(Manually ran it in DB, otherwise have to make a new migration V3 file)
-- Note: We can't directly alter existing enum values in PostgreSQL, so we add new values if they don't exist and set the default to 'NEW'.
-- ALTER TYPE order_status ADD VALUE IF NOT EXISTS 'NEW';
-- ALTER TYPE order_status ADD VALUE IF NOT EXISTS 'EXECUTED';
-- ALTER TYPE order_status ADD VALUE IF NOT EXISTS 'CANCELLED';
ALTER TABLE orders ALTER COLUMN status SET DEFAULT 'NEW';

-- orders: fix order_type enum
ALTER TYPE order_type ADD VALUE IF NOT EXISTS 'MARKET';
ALTER TYPE order_type ADD VALUE IF NOT EXISTS 'LIMIT';

-- outbox_event: rename columns
ALTER TABLE outbox_event RENAME COLUMN aggregate_id TO order_id;
ALTER TABLE outbox_event RENAME COLUMN event_type TO topic;

-- outbox_event: replace published boolean with event_status
ALTER TABLE outbox_event DROP COLUMN published;
ALTER TABLE outbox_event ADD COLUMN event_status VARCHAR(20) NOT NULL DEFAULT 'PENDING';