CREATE TABLE app_user (
    id                            UUID                        PRIMARY KEY DEFAULT gen_random_uuid(),
    version                       INTEGER,
    creation_date                 TIMESTAMP                   NOT NULL DEFAULT now(),
    last_modified_date            TIMESTAMP                   NOT NULL DEFAULT now(),
    email                         VARCHAR(320)                NOT NULL UNIQUE,
    username                      VARCHAR(40)                 NOT NULL UNIQUE,
    password                      CHAR(60)                    NOT NULL,
    balance                       NUMERIC(18,2)               NOT NULL,
    last_issued_token_revocation  TIMESTAMP                   NOT NULL
);

CREATE TABLE asset (
    id                            UUID                        PRIMARY KEY DEFAULT gen_random_uuid(),
    version                       INTEGER,
    creation_date                 TIMESTAMP                   NOT NULL DEFAULT now(),
    last_modified_date            TIMESTAMP                   NOT NULL DEFAULT now(),
    symbol                        VARCHAR(255)                NOT NULL UNIQUE
);

CREATE UNIQUE INDEX idx__tbl_asset__col_symbol ON asset (symbol);

CREATE TABLE users_assets (
    id                            UUID                        PRIMARY KEY DEFAULT gen_random_uuid(),
    version                       INTEGER,
    creation_date                 TIMESTAMP                   NOT NULL DEFAULT now(),
    last_modified_date            TIMESTAMP                   NOT NULL DEFAULT now(),
    user_id                       UUID                        NOT NULL,
    asset_id                      UUID                        NOT NULL,
    quantity                      NUMERIC(18,8)               NOT NULL,
    total_profit                  NUMERIC(18,2)               NOT NULL,
    FOREIGN KEY (user_id) REFERENCES app_user(id) ON DELETE CASCADE,
    FOREIGN KEY (asset_id) REFERENCES asset(id) ON DELETE CASCADE
);

CREATE INDEX idx__tbl_users_assets__col_user_id ON users_assets (user_id);

CREATE TABLE trade (
    id                            UUID                        PRIMARY KEY DEFAULT gen_random_uuid(),
    version                       INTEGER,
    creation_date                 TIMESTAMP                   NOT NULL DEFAULT now(),
    last_modified_date            TIMESTAMP                   NOT NULL DEFAULT now(),
    user_id                       UUID                        NOT NULL,
    symbol                        VARCHAR(255)                NOT NULL,
    trade_type                    VARCHAR(50)                 NOT NULL,
    quantity                      NUMERIC(10,8)               NOT NULL,
    total_price                   NUMERIC(18,2)               NOT NULL,
    FOREIGN KEY (user_id) REFERENCES app_user(id) ON DELETE CASCADE
);

CREATE INDEX idx__tbl_trade__col_user_id ON trade(user_id);