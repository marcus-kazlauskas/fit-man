--liquibase formatted sql

--changeset github.com/marcus-kazlauskas:1
create sequence event_seq start with 1 increment by 50;

--changeset github.com/marcus-kazlauskas:2
create table if not exists event (
    id bigint default nextval('event_seq') primary key,
    event_time timestamp,
    event_name text,
    event_type text,
    activity_id bigint not null references activity
);

--changeset github.com/marcus-kazlauskas:3
comment on table event is 'Timer data from Event part of a .fit file';