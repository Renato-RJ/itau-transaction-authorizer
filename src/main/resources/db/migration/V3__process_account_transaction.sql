-- V2: stored procedure to process account transaction atomically
-- This function locks the account row (FOR UPDATE), validates currency and sufficiency
-- and updates the balance atomically, returning the new balance.
SET search_path TO public;

CREATE OR REPLACE FUNCTION public.process_account_transaction(
    p_account_id VARCHAR,
    p_amount NUMERIC,
    p_currency VARCHAR,
    p_type VARCHAR
) RETURNS TABLE(new_balance NUMERIC) AS $$
DECLARE
    cur_balance NUMERIC;
    cur_currency VARCHAR;
    v_new_balance NUMERIC;
BEGIN
    SELECT balance_amount, balance_currency
    INTO cur_balance, cur_currency
    FROM public.accounts
    WHERE id = p_account_id
    FOR UPDATE;

    IF cur_currency IS NULL THEN
        RAISE EXCEPTION 'Conta não encontrada: %', p_account_id;
    END IF;

    IF cur_currency <> p_currency THEN
        RAISE EXCEPTION 'Currency mismatch: expected % but got %', cur_currency, p_currency;
    END IF;

    IF p_type = 'DEBIT' THEN
        IF cur_balance < p_amount THEN
            RAISE EXCEPTION 'Insufficient funds' USING DETAIL = cur_balance;
        END IF;
        v_new_balance := cur_balance - p_amount;
    ELSIF p_type = 'CREDIT' THEN
        v_new_balance := cur_balance + p_amount;
    ELSE
        RAISE EXCEPTION 'Invalid transaction type: %', p_type;
    END IF;

    UPDATE public.accounts
    SET balance_amount = v_new_balance
    WHERE id = p_account_id;

    new_balance := v_new_balance;
    RETURN NEXT;
END;
$$ LANGUAGE plpgsql;
