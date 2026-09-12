CREATE TABLE idempotency_records (
    idempotency_key VARCHAR(255) PRIMARY KEY,
    request_hash VARCHAR(255) NOT NULL,
    transaction_id UUID,
    response_status INT NOT NULL,
    response_body JSONB,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE reconciliation_results (
    id UUID PRIMARY KEY,
    wallet_id UUID NOT NULL REFERENCES wallets(id),
    ledger_balance BIGINT NOT NULL,
    wallet_balance BIGINT NOT NULL,
    difference BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL,
    checked_at TIMESTAMP WITH TIME ZONE NOT NULL
);
