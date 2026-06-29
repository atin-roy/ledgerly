CREATE TABLE app_user (
    id         BIGSERIAL PRIMARY KEY,
    username   VARCHAR(50)  NOT NULL UNIQUE,
    email      VARCHAR(100) NOT NULL UNIQUE,
    password   VARCHAR(100) NOT NULL,
    role       VARCHAR(20)  NOT NULL,
    created_at TIMESTAMP    NOT NULL,
    updated_at TIMESTAMP    NOT NULL
);

CREATE TABLE category (
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(50)  NOT NULL,
    user_id    BIGINT       NOT NULL REFERENCES app_user(id),
    created_at TIMESTAMP    NOT NULL,
    updated_at TIMESTAMP    NOT NULL,
    UNIQUE (name, user_id)
);

CREATE TABLE party (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(100) NOT NULL,
    normalized_name VARCHAR(100) NOT NULL,
    user_id         BIGINT       NOT NULL REFERENCES app_user(id),
    created_at      TIMESTAMP    NOT NULL,
    updated_at      TIMESTAMP    NOT NULL,
    UNIQUE (normalized_name, user_id)
);

CREATE TABLE transactions (
    id          BIGSERIAL       PRIMARY KEY,
    amount      NUMERIC(19, 2)  NOT NULL,
    date        TIMESTAMP       NOT NULL,
    description VARCHAR(500),
    category_id BIGINT          NOT NULL REFERENCES category(id),
    user_id     BIGINT          NOT NULL REFERENCES app_user(id),
    party_id    BIGINT          REFERENCES party(id),
    created_at  TIMESTAMP       NOT NULL,
    updated_at  TIMESTAMP       NOT NULL
);

CREATE INDEX idx_transaction_user_date ON transactions (user_id, date);

CREATE TABLE budget (
    id          BIGSERIAL      PRIMARY KEY,
    amount      NUMERIC(19, 2) NOT NULL,
    category_id BIGINT         NOT NULL UNIQUE REFERENCES category(id),
    user_id     BIGINT         NOT NULL REFERENCES app_user(id),
    created_at  TIMESTAMP      NOT NULL,
    updated_at  TIMESTAMP      NOT NULL
);

CREATE TABLE bill (
    id         BIGSERIAL      PRIMARY KEY,
    name       VARCHAR(100)   NOT NULL,
    amount     NUMERIC(19, 2) NOT NULL,
    due_date   TIMESTAMP      NOT NULL,
    status     VARCHAR(20)    NOT NULL,
    user_id    BIGINT         NOT NULL REFERENCES app_user(id),
    created_at TIMESTAMP      NOT NULL,
    updated_at TIMESTAMP      NOT NULL
);

CREATE INDEX idx_bill_due_date ON bill (due_date);

CREATE TABLE pot (
    id         BIGSERIAL      PRIMARY KEY,
    name       VARCHAR(100)   NOT NULL,
    target     NUMERIC(19, 2) NOT NULL,
    saved      NUMERIC(19, 2) NOT NULL,
    user_id    BIGINT         NOT NULL REFERENCES app_user(id),
    created_at TIMESTAMP      NOT NULL,
    updated_at TIMESTAMP      NOT NULL,
    UNIQUE (user_id, name)
);
