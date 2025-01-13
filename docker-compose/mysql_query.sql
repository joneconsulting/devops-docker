show databases;
create database mydb1;
use mydb1;
show tables;
create table mytable(id int auto_increment primary key, name varchar(20) not null, created_at datetime default now());
insert into mytable(name) values('Dowon Lee');
select * from mytable;

# add catalog
insert into catalog(product_id, product_name, unit_price, stock) values('CATALOG-004', 'Sydney', 3000, 300)