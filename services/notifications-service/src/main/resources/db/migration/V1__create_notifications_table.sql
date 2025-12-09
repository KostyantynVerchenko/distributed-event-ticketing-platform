CREATE TABLE IF NOT EXISTS notifications (
                                             id          UUID PRIMARY KEY,
                                             user_id     UUID        NOT NULL,
                                             order_id    UUID,
                                             type        VARCHAR(64) NOT NULL,
                                             message     VARCHAR(255) NOT NULL,
                                             status      VARCHAR(32) NOT NULL,
                                             created_at  TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);