CREATE TABLE IF NOT EXISTS wow_character(
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(32) NOT NULL,
    region          VARCHAR(8) NOT NULL CHECK (region IN ('eu', 'us', 'kr', 'tw')),
    realm_slug      VARCHAR(64) NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_wow_character
    ON wow_character (region, realm_slug, lower(name));
