CREATE TABLE Person (
    nif VARCHAR(255),
    name VARCHAR(255)
);

CREATE TABLE Patient (
    nif VARCHAR(255),
    healthCardId VARCHAR(255)
);

CREATE TABLE Doctor (
    nif VARCHAR(255),
    medicalLicense VARCHAR(255)
);

CREATE TABLE Appointment (
    id INT,
    date_ INT,
    PRIMARY KEY (id)
);

CREATE TABLE attends (
    nif INT,
    id INT
);

CREATE TABLE schedules (
    nif INT,
    id INT
);

