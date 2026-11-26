create table if not exists members (
    id uuid primary key,
    email varchar(254) not null unique,
    password_hash varchar(255) not null,
    role varchar(20) not null
);
