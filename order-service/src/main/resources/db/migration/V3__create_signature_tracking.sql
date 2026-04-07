CREATE TABLE signature_tracking
(
    id           UUID PRIMARY KEY,
    order_id     UUID NOT NULL,
    envelope_id  VARCHAR(255),
    signer_email VARCHAR(255),
    status       VARCHAR(50),
    signing_url  TEXT,
    created_at   TIMESTAMP,
    updated_at   TIMESTAMP
);