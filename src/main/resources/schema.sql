create table if not exists USER
(
    id bigint primary key auto_increment,
    fullName varchar(63) not null,
    email varchar(255) unique not null,
    password varchar(22) not null,
    phoneNumber varchar(12) not null,
    upiId varchar(63)
);

create table if not exists UPLOAD
(
    id bigint primary key auto_increment,
    userId bigint references USER(id),
    filename varchar(255)
);