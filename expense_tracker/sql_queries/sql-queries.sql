create schema expense_tracker;
use expense_tracker;

drop table if exists userAccount;
create table if not exists userAccount(
accountId int auto_increment primary key,
firstName char(20) not null,
lastName char(20) not null,
username char(20) unique not null,
birthday Date not null,
password varchar(100) not null,
email char(50) unique not null,
system_date datetime default current_timestamp not null
);

drop table if exists income;
create table if not exists income(
accountId int not null,
transactionId char(36) primary key,
type char(10) not null,
amount double not null,
source char(50) not null,
description varchar(100) not null,
date date not null,
system_date datetime default current_timestamp not null,
foreign key (accountId) references userAccount(accountId)
on update cascade on delete cascade
);

drop table if exists expenses;
create table if not exists expenses(
accountId int not null,
transactionId char(36) primary key,
type char(10) not null,
amount double not null,
category char(50) not null,
description varchar(100) not null,
date date not null,
system_date datetime default current_timestamp not null,
foreign key (accountId) references userAccount(accountId)
on update cascade on delete cascade
);

select * from userAccount;
select * from income;
select * from expenses;