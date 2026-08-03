-- Every domain table is listed by owner (findByUser_Id); user_id had no index
-- on any of these except transactions, so each list query was a sequential scan.
CREATE INDEX idx_category_user ON category (user_id);
CREATE INDEX idx_party_user ON party (user_id);
CREATE INDEX idx_budget_user ON budget (user_id);
CREATE INDEX idx_bill_user ON bill (user_id);
CREATE INDEX idx_pot_user ON pot (user_id);
