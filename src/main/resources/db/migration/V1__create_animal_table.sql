-- Table: animal
CREATE TABLE animal
(
  tag_no         VARCHAR(10) PRIMARY KEY, -- matches @Id @AnimalTagId
  name           VARCHAR(255) NOT NULL,
  color          VARCHAR(50),             -- Enum stored as string
  type           VARCHAR(50),             -- Enum stored as string
  gender         VARCHAR(50),             -- Enum stored as string
  date_of_birth  DATE,
  birth_weight   DOUBLE PRECISION,
  current_weight DOUBLE PRECISION,
  mother_id      VARCHAR(10),             -- FK to animal.tag_no
  father_id      VARCHAR(10),             -- FK to animal.tag_no
  CONSTRAINT fk_mother FOREIGN KEY (mother_id) REFERENCES animal (tag_no),
  CONSTRAINT fk_father FOREIGN KEY (father_id) REFERENCES animal (tag_no)
);
