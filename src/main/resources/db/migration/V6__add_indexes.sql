-- V6: Indexes for FK / frequently queried columns

CREATE INDEX idx_groups_cate_id            ON groups(cate_id);
CREATE INDEX idx_group_members_group_id    ON group_members(group_id);
CREATE INDEX idx_group_members_user_id     ON group_members(user_id);
CREATE INDEX idx_group_invitations_group   ON group_invitations(group_id);
CREATE INDEX idx_group_invitations_invitee ON group_invitations(invitee_id);
CREATE INDEX idx_expenses_group_id         ON expenses(group_id);
CREATE INDEX idx_expenses_cate_id          ON expenses(cate_id);
CREATE INDEX idx_expense_payers_expense_id ON expense_payers(expense_id);
CREATE INDEX idx_expense_payers_user_id    ON expense_payers(user_id);
CREATE INDEX idx_expense_shares_expense_id ON expense_shares(expense_id);
CREATE INDEX idx_expense_shares_user_id    ON expense_shares(user_id);
CREATE INDEX idx_debts_expense_id          ON debts(expense_id);
CREATE INDEX idx_debts_from_user           ON debts(from_user_id);
CREATE INDEX idx_debts_to_user             ON debts(to_user_id);
CREATE INDEX idx_settlements_group_id      ON settlements(group_id);
CREATE INDEX idx_activities_user_id        ON activities(user_id);
CREATE INDEX idx_activities_entity         ON activities(entity_type, entity_id);
