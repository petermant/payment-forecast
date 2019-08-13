drop table if exists MERCHANT;
drop table if exists PAYER;
drop table if exists PAYMENT;

create table MERCHANT
(
  ID            DECIMAL(19, 2) not null
    primary key,
  MERCHANT_NAME VARCHAR(255)   not null,
  PUBLIC_KEY    VARCHAR(255)   not null
);

create table PAYER
(
  ID               DECIMAL(19, 2) not null
    primary key,
  PAYER_PUBLIC_KEY VARCHAR(255)   not null
);

create table PAYMENT
(
  ID                  DECIMAL(19, 2) not null
    primary key,
  AMOUNT              DECIMAL(19, 2) not null,
  CURRENCY            INTEGER        not null,
  DEBIT_PERMISSION_ID DECIMAL(19, 2) not null,
  DUE_EPOC            TIMESTAMP      not null,
  RECEIVEDUTC         TIMESTAMP      not null,
  MERCHANT_ID         DECIMAL(19, 2) not null,
  PAYER_ID            DECIMAL(19, 2) not null,
  constraint FK8KAFA0BF5K60MKTAP1SGBK47M
    foreign key (PAYER_ID) references PAYER,
  constraint FKGI8GWSTWXKVP33LIY8GUN03HS
    foreign key (MERCHANT_ID) references MERCHANT
);

