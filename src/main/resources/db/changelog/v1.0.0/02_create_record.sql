--liquibase formatted sql

--changeset github.com/marcus-kazlauskas:1
create table if not exists record (
    id bigint primary key generated always as identity,
    position_lat double precision,
    position_long double precision,
    distance double precision,
    enhanced_speed double precision,
    enhanced_altitude double precision,
    mark smallint not null
        default 1,
    activity_id bigint not null
        constraint fk_activity_id references activity
);

--changeset github.com/marcus-kazlauskas:2
comment on table record is 'Geo data from Record part of a .fit file'