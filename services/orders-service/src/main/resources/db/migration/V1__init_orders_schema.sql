CREATE TYPE order_currency AS ENUM ('USD', 'EUR', 'UAH');
CREATE TYPE order_item_status AS ENUM ('RESERVED', 'CONFIRMED', 'RELEASED');
CREATE TYPE order_status AS ENUM ('CREATED', 'PENDING_PAYMENT', 'PAID', 'CANCELLED', 'EXPIRED', 'FAILED');
CREATE TYPE outbox_event_status AS ENUM ('NEW', 'SENT', 'FAILED');

CREATE TABLE orders (
                        id UUID PRIMARY KEY,
                        user_id UUID NOT NULL,
                        order_status order_status NOT NULL,
                        total_amount NUMERIC(19, 2) NOT NULL,
                        order_currency order_currency NOT NULL,
                        reserved_until TIMESTAMP,
                        payment_id UUID,
                        created_at TIMESTAMP NOT NULL DEFAULT now(),
                        updated_at TIMESTAMP,
                        version BIGINT
);

CREATE TABLE order_item (
                            id UUID PRIMARY KEY,
                            order_id UUID,
                            event_id UUID NOT NULL,
                            quantity INTEGER,
                            unit_price NUMERIC(19, 2),
                            status order_item_status,
                            event_title_snapshot VARCHAR(255),
                            event_date_snapshot TIMESTAMP,
                            ticket_price_snapshot NUMERIC(19, 2)
);

CREATE TABLE ticket_reservation (
                                    id UUID PRIMARY KEY,
                                    event_id UUID NOT NULL,
                                    total_tickets INTEGER NOT NULL,
                                    reserved_tickets INTEGER NOT NULL,
                                    sold_tickets INTEGER NOT NULL,
                                    created_at TIMESTAMP NOT NULL DEFAULT now(),
                                    updated_at TIMESTAMP,
                                    version BIGINT
);

CREATE TABLE outbox_event (
                              id UUID PRIMARY KEY,
                              aggregate_type VARCHAR(255) NOT NULL,
                              aggregate_id UUID NOT NULL,
                              event_type VARCHAR(255) NOT NULL,
                              payload TEXT NOT NULL,
                              status outbox_event_status NOT NULL,
                              created_at TIMESTAMP NOT NULL DEFAULT now(),
                              updated_at TIMESTAMP
);

ALTER TABLE order_item
    ADD CONSTRAINT fk_order_item_order
        FOREIGN KEY (order_id) REFERENCES orders (id);

ALTER TABLE ticket_reservation
    ADD CONSTRAINT uq_ticket_reservation_event_id
        UNIQUE (event_id);

CREATE INDEX idx_orders_user_id ON orders (user_id);
CREATE INDEX idx_orders_status_reserved_until ON orders (order_status, reserved_until);
CREATE INDEX idx_order_item_order_id ON order_item (order_id);
CREATE INDEX idx_outbox_event_status_created_at ON outbox_event (status, created_at);
