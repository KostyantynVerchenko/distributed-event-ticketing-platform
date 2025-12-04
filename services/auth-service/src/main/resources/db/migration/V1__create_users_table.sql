CREATE TABLE IF NOT EXISTS users (
                                     id          UUID PRIMARY KEY,
                                     email       VARCHAR(255),
                                     password    VARCHAR(255),
                                     created_at  TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                                     updated_at  TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);
