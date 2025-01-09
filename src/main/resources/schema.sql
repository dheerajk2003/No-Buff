create table if not exists USER
(
    id bigint primary key auto_increment,
    fullName varchar(63) not null,
    email varchar(255) unique not null,
    password varchar(22) not null
);

create table if not exists UPLOAD
(
    id bigint primary key auto_increment,
    userId bigint references USER(id),
    filename varchar(255),
    image varchar(255)
);