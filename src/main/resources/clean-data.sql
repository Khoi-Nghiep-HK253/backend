-- =============================================================================
-- DIVVY DEV DATA CLEANUP SCRIPT (Wipe all data & Reset sequences back to 1)
-- =============================================================================

BEGIN;

TRUNCATE TABLE 
    activities,
    expense_shares,
    expense_payers,
    expenses,
    group_invitations,
    group_members,
    groups,
    users,
    categories,
    currency,
    surveys,
    password_reset_tokens
RESTART IDENTITY CASCADE;

COMMIT;