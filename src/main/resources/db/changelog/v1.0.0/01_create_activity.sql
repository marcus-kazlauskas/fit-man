--liquibase formatted sql

--changeset github.com/marcus-kazlauskas:1
create table if not exists activity (
    id bigint generated always as identity primary key,
    end_time timestamp with time zone not null,
    start_time timestamp with time zone not null,
    sport text not null,
    total_elapsed_time interval day to second,
    total_timer_time interval day to second,
    total_distance real,
    total_calories integer,
    total_ascent integer,
    enhanced_avg_speed real,
    enhanced_max_speed real,
    user_name text not null,
    device_name text not null,
    marked boolean not null
);

--changeset github.com/marcus-kazlauskas:2
comment on table activity is 'User''s activity from Session, Activity, Sport, User Profile, Device Info parts of a .fit file';

--changeset github.com/marcus-kazlauskas:3
create index if not exists start_time_idx on activity (start_time);