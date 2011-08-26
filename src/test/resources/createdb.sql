CREATE TABLE aggregation (
    id varchar(255) NOT NULL,
    exchange blob NOT NULL,
    constraint aggregation_pk PRIMARY KEY (id)
);
CREATE TABLE aggregation_completed (
    id varchar(255) NOT NULL,
    exchange blob NOT NULL,
    constraint aggregation_completed_pk PRIMARY KEY (id)
);
CREATE TABLE balance(
    account VARCHAR NOT NULL,
    saldo number,
    constraint balance_pk PRIMARY KEY (account)
);
insert into balance(account, saldo) values ('11111111111', 2000000);

CREATE TABLE reserved(
    tx VARCHAR NOT NULL,
    account VARCHAR NOT NULL,
    saldo number,
    status number,
    created timestamp
);
CREATE SEQUENCE BALANCE_SEQ;