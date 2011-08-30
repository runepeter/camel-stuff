

CREATE TABLE IF NOT EXISTS aggregation (
    id varchar(255) NOT NULL,
    exchange blob NOT NULL,
    constraint aggregation_pk PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS  aggregation_completed (
    id varchar(255) NOT NULL,
    exchange blob NOT NULL,
    constraint aggregation_completed_pk PRIMARY KEY (id)
);
CREATE TABLE  IF NOT EXISTS balance(
    account VARCHAR NOT NULL,
    saldo number,
    constraint balance_pk PRIMARY KEY (account)
);

CREATE TABLE IF NOT EXISTS reserved(
    tx VARCHAR NOT NULL,
    account VARCHAR NOT NULL,
    saldo number,
    status number,
    created timestamp
);
CREATE SEQUENCE IF NOT EXISTS BALANCE_SEQ;