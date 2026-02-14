-- Table: health_record
CREATE TABLE health_record
(
  id            BIGSERIAL PRIMARY KEY, -- matches @GeneratedValue(strategy = IDENTITY)
  animal_tag_no VARCHAR(10) NOT NULL,  -- FK to animal.tag_no
  treatment_id  BIGINT,                -- FK to treatment.id, nullable if no treatment
  date          DATE,
  dosage        VARCHAR(50),
  next_due      DATE,
  notes         TEXT,
  status        VARCHAR(20),

  CONSTRAINT fk_health_animal FOREIGN KEY (animal_tag_no) REFERENCES animal (tag_no),
  CONSTRAINT fk_health_treatment FOREIGN KEY (treatment_id) REFERENCES treatment (id)
);
