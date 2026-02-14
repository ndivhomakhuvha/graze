CREATE TABLE animal_sequence
(
  gender      CHAR(1) NOT NULL PRIMARY KEY,
  last_number INT     NOT NULL DEFAULT 0
);

-- Seed initial values
INSERT INTO animal_sequence (gender, last_number)
VALUES ('M', 0);
INSERT INTO animal_sequence (gender, last_number)
VALUES ('F', 0);
