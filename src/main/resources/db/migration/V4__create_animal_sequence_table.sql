CREATE TABLE graze.animal_sequence
(
  gender      VARCHAR(10) NOT NULL PRIMARY KEY,
  next_value  INT         NOT NULL DEFAULT 1
);

-- Seed initial values
INSERT INTO graze.animal_sequence (gender, next_value)
VALUES ('MALE', 1);
INSERT INTO graze.animal_sequence (gender, next_value)
VALUES ('FEMALE', 1);
