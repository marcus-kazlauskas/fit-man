--liquibase formatted sql

--changeset github.com/marcus-kazlauskas:1
create sequence record_seq start 1 increment by 50;

--changeset github.com/marcus-kazlauskas:2
create table if not exists record (
    id bigint primary key default nextval('record_seq'),
    position_lat double precision,
    position_long double precision,
    distance float,
    enhanced_speed float,
    enhanced_altitude float,
    mark smallint not null
        default 1,
    activity_id bigint not null
        references activity
);

--changeset github.com/marcus-kazlauskas:3
comment on table record is 'Geo data from Record part of a .fit file';