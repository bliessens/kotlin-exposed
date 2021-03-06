CREATE SEQUENCE PK_SEQUENCE START 1 INCREMENT 1;

CREATE TABLE QUOTE
(
    ID           BIGINT PRIMARY KEY,
    QUOTE_NUMBER VARCHAR(25)
);

CREATE TABLE OPTION
(
    ID       BIGINT PRIMARY KEY,
    QUOTE_ID BIGINT      NOT NULL,
    NAME     VARCHAR(25) NOT NULL,
    CONSTRAINT OPTION_FK FOREIGN KEY (QUOTE_ID) REFERENCES QUOTE (ID)
);