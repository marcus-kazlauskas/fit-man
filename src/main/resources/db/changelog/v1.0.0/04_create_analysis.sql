--liquibase formatted sql

--changeset github.com/marcus-kazlauskas:1
create table if not exists analysis (
    id bigint generated always as identity primary key,
    total_distance real,
    moving_time interval day to second,
    average_speed real,
    activity_id bigint not null references activity
);

--changeset github.com/marcus-kazlauskas:2
comment on table analysis is 'Activity analysis data';