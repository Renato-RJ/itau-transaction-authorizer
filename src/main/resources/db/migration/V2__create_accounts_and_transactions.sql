-- V1: create accounts and transactions tables
SET search_path TO public;

-- Accounts table
CREATE TABLE IF NOT EXISTS public.accounts (
    id VARCHAR(36) PRIMARY KEY,
    owner_id VARCHAR(255) NOT NULL,
    balance_amount NUMERIC(19,2) NOT NULL,
    balance_currency VARCHAR(3) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    status VARCHAR(50) NOT NULL
);

-- Transactions table
CREATE TABLE IF NOT EXISTS public.transactions (
    id VARCHAR(36) PRIMARY KEY,
    account_id VARCHAR(36) NOT NULL,
    type VARCHAR(20) NOT NULL,
    amount NUMERIC(19,2) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    status VARCHAR(20) NOT NULL,
    transaction_timestamp TIMESTAMP WITH TIME ZONE NOT NULL,
    rejection_reason VARCHAR(1000),
    CONSTRAINT fk_account
      FOREIGN KEY(account_id)
        REFERENCES public.accounts(id)
        ON DELETE CASCADE
);

-- Indexes to help queries
CREATE INDEX IF NOT EXISTS idx_transactions_account_id ON public.transactions(account_id);


