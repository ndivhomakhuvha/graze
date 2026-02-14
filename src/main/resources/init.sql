create table animal_sequence (
                               gender varchar(10) primary key,
                               next_value bigint not null
);
insert into animal_sequence (gender, next_value) values ('MALE', 1);
insert into animal_sequence (gender, next_value) values ('FEMALE', 1);
