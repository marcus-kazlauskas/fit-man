\c "postgres"

CREATE ROLE "fit-man" WITH LOGIN PASSWORD 'fit-man';

CREATE DATABASE "fit-man-db" WITH OWNER "fit-man";

\q
