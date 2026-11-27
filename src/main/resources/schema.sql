create table if not exists members (
    id uuid primary key,
    email varchar(254) not null unique,
    password_hash varchar(255) not null,
    role varchar(20) not null
);

alter table members add constraint if not exists uq_member_id_role unique (id, role);

create table if not exists offices (
    id uuid primary key,
    member_id uuid not null unique,
    member_role varchar(20) default 'OFFICE' not null check (member_role = 'OFFICE'),
    name varchar not null,
    foreign key (member_id, member_role) references members (id, role)
);

create table if not exists labor_requests (
    id uuid primary key,
    office_id uuid not null references offices (id),
    employer_id uuid not null references members (id),
    starts_at timestamp(9) not null,
    ends_at timestamp(9) not null,
    work_description varchar not null,
    worker_count integer not null check (worker_count > 0),
    daily_wage bigint not null check (daily_wage > 0),
    check (starts_at < ends_at)
);
