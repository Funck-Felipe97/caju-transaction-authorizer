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
CREATE TABLE INT_LOCK  (
	LOCK_KEY CHAR(36) NOT NULL,
	REGION VARCHAR(100) NOT NULL,
	CLIENT_ID CHAR(36),
	CREATED_DATE TIMESTAMP NOT NULL,
	constraint INT_LOCK_PK primary key (LOCK_KEY, REGION)
);