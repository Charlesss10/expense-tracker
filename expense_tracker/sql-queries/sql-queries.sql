create schema expense_tracker;
use expense_tracker;

drop table income;
create table if not exists income(
id int auto_increment primary key,
type char(10),
amount double,
source varchar(20),
transaction_date date,
system_date datetime default current_timestamp
);

drop table expenses;
create table if not exists expenses(
id int auto_increment primary key,
type char(10),
amount double,
category varchar(20),
transaction_date date,
system_date datetime default current_timestamp
);

drop view recentIncome;
create or replace view recentIncome as
select id, type, amount, source, transaction_date, system_date
from income
where 
system_date >= date_sub(now(), interval 30 day);

drop view recentExpenses;
create or replace view recentExpenses as
select id, type, amount, category, transaction_date, system_date
from expenses
where
system_date >= date_sub(now(), interval 30 day);

select * from income;
select * from expenses;
select * from recentIncome;
select * from recentExpenses;