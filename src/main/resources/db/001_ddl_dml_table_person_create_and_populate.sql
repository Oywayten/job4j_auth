CREATE TABLE person (
    id       serial PRIMARY KEY NOT NULL,
    login    varchar(2000),
    password varchar(2000)
);

insert into person (login, password) values ('parsentev', '123');
insert into person (login, password) values ('ban', '123');
insert into person (login, password) values ('ivan', '123');