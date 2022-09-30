drop database if exists evms;
create database if not exists evms;
use evms;

drop table if exists vaccine_type;
create table vaccine_type (
name varchar(30) not null primary key,
description varchar(256) not null,
created_date datetime not null default current_timestamp,
created_by varchar(100) not null,
last_updated_date datetime not null default current_timestamp,
last_updated_by varchar(100) not null);

insert into vaccine_type values
('COVID19',	'COVID19 - It Typically Requires 2 Shots', current_timestamp, 'Mohammed Monirul Islam', current_timestamp, 'Mohammed Monirul Islam'),
('EBOLA', 	'EBOLA - It Typically Requires 2 Shots', current_timestamp, 'Mohammed Monirul Islam', current_timestamp, 'Mohammed Monirul Islam'),
('FLU',	'FLU - It Typically Requires 1 Shot', current_timestamp, 'Mohammed Monirul Islam', current_timestamp, 'Mohammed Monirul Islam');

drop table if exists users;
create table users (
  user_id int(11) not null auto_increment primary key,
  username varchar(45) not null,
  password varchar(64) not null,
  role varchar(45) not null,
  enabled tinyint(4) default null,
  created_date datetime not null default current_timestamp,
  created_by varchar(100) not null,
  last_updated_date datetime not null default current_timestamp,
  last_updated_by varchar(100) not null
);

-- Password:  tset
insert into users (username, password, role, enabled, created_date, created_by, last_updated_date, last_updated_by) values
('user', '$2y$10$bmTgt1b7sTw81sb3Vxl8duftevcYHSgAVYD1qZVZKxOGHE3oO3KCG', 'ROLE_USER', 1, current_timestamp, 'Mohammed Monirul Islam', current_timestamp, 'Mohammed Monirul Islam'),
('admin', '$2y$10$bmTgt1b7sTw81sb3Vxl8duftevcYHSgAVYD1qZVZKxOGHE3oO3KCG', 'ROLE_ADMIN', 1, current_timestamp, 'Mohammed Monirul Islam', current_timestamp, 'Mohammed Monirul Islam');