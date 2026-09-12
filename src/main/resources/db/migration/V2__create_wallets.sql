CREATE TABLE wallets (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id),
    currency VARCHAR(3) NOT NULL,
    balance BIGINT NOT NULL DEFAULT 0 CHECK (balance >= 0),
    status VARCHAR(50) NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT unique_user_currency UNIQUE(user_id, currency)
);

CREATE INDEX idx_wallets_user_id ON wallets(user_id);
