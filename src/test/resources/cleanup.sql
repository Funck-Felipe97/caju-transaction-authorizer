DELETE FROM account_transaction;
DELETE FROM account_balance;
DELETE FROM account;
DELETE FROM merchant;

ALTER TABLE account_balance ALTER COLUMN id RESTART WITH 1;
ALTER TABLE account_transaction ALTER COLUMN id RESTART WITH 1;
ALTER TABLE merchant ALTER COLUMN id RESTART WITH 1;

