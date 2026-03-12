CREATE TABLE graze.animal_owners
(
  animal_tag_no VARCHAR(10) NOT NULL,
  user_id       UUID        NOT NULL,
  role          VARCHAR(20) NOT NULL,
  owned_since   DATE        NOT NULL,

  PRIMARY KEY (animal_tag_no, user_id),

  CONSTRAINT fk_owner_animal FOREIGN KEY (animal_tag_no) REFERENCES graze.animal (tag_no),
  CONSTRAINT fk_owner_user   FOREIGN KEY (user_id)       REFERENCES graze.users (id)
);
