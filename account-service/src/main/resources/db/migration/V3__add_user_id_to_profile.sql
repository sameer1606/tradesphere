-- V3: Add user_id to account_profile
-- Links account profile to Identity Service user via shared UUID (no FK across services)

ALTER TABLE account_profile ADD COLUMN user_id UUID NOT NULL;

CREATE UNIQUE INDEX idx_account_profile_user_id ON account_profile(user_id);