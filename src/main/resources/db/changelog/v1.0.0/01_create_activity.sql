--liquibase formatted sql

--changeset github.com/marcus-kazlauskas:1
create table if not exists activity (
    id bigint primary key generated always as identity,
    end_time timestamptz not null,
    start_time timestamptz not null,
    sport text not null,
    total_elapsed_time interval,
    total_timer_time interval,
    total_distance real,
    total_calories integer,
    total_ascent integer,
    enhanced_avg_speed real,
    enhanced_max_speed real,
    user_name text not null,
    device_name text not null,
    marked boolean not null
        default false
);

--changeset github.com/marcus-kazlauskas:2
comment on table activity is 'User''s activity from Session, Activity, Sport, User Profile, Device Info parts of a .fit file';

--changeset github.com/marcus-kazlauskas:3
create index if not exists idx_start_time on activity (start_time);