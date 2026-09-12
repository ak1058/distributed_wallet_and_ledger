CREATE TABLE ledger_entries (
    id UUID PRIMARY KEY,
    transaction_id UUID NOT NULL REFERENCES transactions(id),
    wallet_id UUID NOT NULL REFERENCES wallets(id),
    entry_type VARCHAR(10) NOT NULL CHECK (entry_type IN ('DEBIT', 'CREDIT')),
    amount BIGINT NOT NULL CHECK (amount > 0),
    currency VARCHAR(3) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_ledger_entries_wallet_created ON ledger_entries(wallet_id, created_at);
CREATE INDEX idx_ledger_entries_transaction_id ON ledger_entries(transaction_id);
