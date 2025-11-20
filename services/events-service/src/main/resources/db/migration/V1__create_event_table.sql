CREATE TYPE event_status AS ENUM ('CANCELLED', 'AVAILABLE', 'EXPIRED', 'DELETED');

CREATE TABLE event (
                       id BIGSERIAL PRIMARY KEY,
                       title VARCHAR(255) NOT NULL,
                       date DATE NOT NULL,
                       price NUMERIC(19, 2) NOT NULL,
                       tickets_available INTEGER NOT NULL,
                       event_status event_status NOT NULL DEFAULT 'AVAILABLE'
);