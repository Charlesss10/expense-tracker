-- create schema expense_tracker;
-- use expense_tracker;

-- create user account table
drop table if exists userAccount;
create table if not exists userAccount(
accountId int auto_increment primary key,
firstName char(20) not null,
lastName char(20) not null,
username char(20) unique not null,
birthday Date not null,
currency char(10) not null,
password varchar(100) not null,
email char(50) unique not null,
system_date datetime default current_timestamp not null
);

-- create income table
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

-- create expenses table
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

-- create sessions table
drop table if exists sessions;
create table if not exists sessions(
sessionId char(36) primary key,
accountId int not null,
created_at datetime default current_timestamp not null,
expires_at datetime,
foreign key (accountId) references userAccount(accountId)
);

-- trigger for setting the value of expired_at on the sessions table
drop trigger if exists set_expires_at;
delimiter //
create trigger set_expires_at
before insert on sessions
for each row
begin
    if new.expires_at is null then
        set new.expires_at = date_add(new.created_at, interval 24 hour);
    end if;
end;
//
delimiter ;

select * from userAccount;
select * from income;
select * from expenses;
select * from sessions;