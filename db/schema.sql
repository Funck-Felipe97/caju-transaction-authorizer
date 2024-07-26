-- Application Schema
create table if not exists account (
	id serial not null primary key,
	food_balance integer not null,
	meal_balance integer not null,
	cash_balance integer not null,
	total_balance integer not null
);

create table if not exists transaction (
	id serial not null primary key,
	totalAmount integer not null,
	mcc varchar not null,
	merchant varchar not null,
	account_id int not null,
	created_at timestamp not null,
	constraint account_id_fk foreign key (account_id) references account(id)
);

insert into account (id, food_balance, meal_balance, cash_balance, total_balance)
values (1, 1000, 2000, 3000, 6000),
       (2, 500, 600, 700, 1800);