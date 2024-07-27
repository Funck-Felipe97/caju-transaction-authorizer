-- Application Schema
create table if not exists account (
	id serial not null primary key,
	total_balance integer not null
);

create table if not exists account_balance (
    id serial not null primary key,
    account_id integer not null,
    balance_type varchar not null,
    total_balance integer not null,
    constraint account_id_fk foreign key (account_id) references account(id)
);

create table if not exists transaction (
	id serial not null primary key,
	total_amount integer not null,
	mcc varchar not null,
	merchant varchar not null,
	account_id integer not null,
	created_at timestamp not null,
	constraint account_id_fk foreign key (account_id) references account(id)
);

create table if not exists merchant (
    id serial not null primary key,
    mcc varchar not null,
    name varchar not null
);