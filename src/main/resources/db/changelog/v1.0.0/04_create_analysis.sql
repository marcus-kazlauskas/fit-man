--liquibase formatted sql

--changeset github.com/marcus-kazlauskas:1
create table if not exists analysis (
    id bigint generated always as identity primary key,
    total_distance real,
    moving_time bigint,
    average_speed real,
    success boolean not null,
    activity_id bigint not null references activity
);

--changeset github.com/marcus-kazlauskas:2
comment on table analysis is 'Activity analysis data';

--changeset github.com/marcus-kazlauskas:3
create index if not exists activity_id_idx on analysis (activity_id);