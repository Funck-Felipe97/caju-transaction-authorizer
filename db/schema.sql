-- Application schema
create table if not exists account (
	id varchar not null primary key,
	total_balance numeric(12, 2) not null
);

create table if not exists account_balance (
    id serial not null primary key,
    account_id varchar not null,
    balance_type varchar not null,
    total_balance numeric(12, 2) not null,
    constraint account_id_fk foreign key (account_id) references account(id)
);


create table if not exists account_transaction (
	id serial not null primary key,
	total_amount numeric(12, 2) not null,
	mcc varchar not null,
	merchant varchar not null,
	account_id varchar not null,
	created_at timestamp not null,
	constraint account_transaction_account_id_fk foreign key (account_id) references account(id)
);

create table if not exists merchant (
    id serial not null primary key,
    mcc varchar not null,
    name varchar not null
);


-- Schema to use lock with postgres sql
CREATE TABLE if not exists INT_LOCK  (
	LOCK_KEY CHAR(36) NOT NULL,
	REGION VARCHAR(100) NOT NULL,
	CLIENT_ID CHAR(36),
	CREATED_DATE TIMESTAMP NOT NULL,
	constraint INT_LOCK_PK primary key (LOCK_KEY, REGION)
);


-- Inserting data test
insert into account(id, total_balance) select '1', 1000.00;
insert into account_balance (id, account_id, balance_type, total_balance) select 1, '1', 'CASH', 300.00;
insert into account_balance (id, account_id, balance_type, total_balance) select 2, '1', 'MEAL', 200.00;
insert into account_balance (id, account_id, balance_type, total_balance) select 3, '1', 'FOOD', 500.00;

insert into account(id, total_balance) select '2', 2000.00;
insert into account_balance (id, account_id, balance_type, total_balance) select 4, '2', 'FOOD', 2000.00;

insert into account(id, total_balance) select '3', 1500.00;
insert into account_balance (id, account_id, balance_type, total_balance) select 5, '3', 'CASH', 1000.00;
insert into account_balance (id, account_id, balance_type, total_balance) select 6, '3', 'FOOD', 500.00;


insert into merchant(id, mcc, name) select 1, '5411', 'TESTE MERCHANT FOOD 01';
insert into merchant(id, mcc, name) select 2, '5412', 'TESTE MERCHANT FOOD 02';

insert into merchant(id, mcc, name) select 3, '5811', 'TESTE MERCHANT MEAL 01';
insert into merchant(id, mcc, name) select 4, '5812', 'TESTE MERCHANT MEAL 02';

insert into merchant(id, mcc, name) select 5, '3333', 'TESTE MERCHANT CASH 01';
insert into merchant(id, mcc, name) select 6, '2222', 'TESTE MERCHANT CASH 02';