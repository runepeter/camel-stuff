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
    id NUMBER NOT NULL,
    saldo number,
    constraint balance_pk PRIMARY KEY (id)
);
insert into balance values (1, 2000000);

CREATE TABLE reserved(
    saldo number,
    status number,
    created timestamp
);
CREATE SEQUENCE BALANCE_SEQ;