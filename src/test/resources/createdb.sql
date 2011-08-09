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
    transaction VARCHAR2(255) NOT NULL,
    constraint balance_pk PRIMARY KEY (id)
);
CREATE SEQUENCE BALANCE_SEQ;