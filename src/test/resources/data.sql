insert into account(id, total_balance) select '1', 1000.00;
insert into account_balance (id, account_id, balance_type, total_balance) select 1, '1', 'CASH', 300.00;
insert into account_balance (id, account_id, balance_type, total_balance) select 2, '1', 'MEAL', 200.00;
insert into account_balance (id, account_id, balance_type, total_balance) select 3, '1', 'FOOD', 500.00;