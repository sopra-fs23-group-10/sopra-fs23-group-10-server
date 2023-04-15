CREATE SEQUENCE hibernate_sequence START WITH 1 INCREMENT BY 1;

CREATE TABLE PLAYER (
    ID BIGINT DEFAULT hibernate_sequence.nextval PRIMARY KEY,
    USERNAME VARCHAR(255) NOT NULL UNIQUE,
    EMAIL VARCHAR(255) NOT NULL UNIQUE,
    PASSWORD VARCHAR(255) NOT NULL,
    POINTS BIGINT NOT NULL,
    STATUS BIGINT NOT NULL,
    BACKGROUND_MUSIC VARCHAR(20) NOT NULL,
    PROFILE_PICTURE VARCHAR(255) NOT NULL,
    TOKEN VARCHAR(255) NOT NULL UNIQUE
);