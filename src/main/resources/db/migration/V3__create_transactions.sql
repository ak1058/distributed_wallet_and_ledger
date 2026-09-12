CREATE TABLE transactions (
    id UUID PRIMARY KEY,
    idempotency_key VARCHAR(255) NOT NULL UNIQUE,
    source_wallet_id UUID REFERENCES wallets(id),
    destination_wallet_id UUID REFERENCES wallets(id),
    amount BIGINT NOT NULL CHECK (amount > 0),
    currency VARCHAR(3) NOT NULL,
    status VARCHAR(50) NOT NULL,
    failure_reason TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT check_wallets_different CHECK (source_wallet_id IS NULL OR destination_wallet_id IS NULL OR source_wallet_id != destination_wallet_id)
);

CREATE INDEX idx_transactions_idempotency_key ON transactions(idempotency_key);
CREATE INDEX idx_transactions_source_created ON transactions(source_wallet_id, created_at);
CREATE INDEX idx_transactions_dest_created ON transactions(destination_wallet_id, created_at);
CREATE INDEX idx_transactions_status_created ON transactions(status, created_at);
