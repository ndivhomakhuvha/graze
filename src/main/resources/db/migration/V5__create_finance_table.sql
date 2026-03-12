-- Table: finance
CREATE TABLE graze.finance
(
  id               BIGSERIAL PRIMARY KEY,
  transaction_type VARCHAR(20)    NOT NULL,
  category         VARCHAR(50)    NOT NULL,
  amount           NUMERIC(12, 2) NOT NULL,
  date             DATE           NOT NULL,
  description      TEXT,
  animal_tag_no    VARCHAR(10),
  reference_id     VARCHAR(100),
  CONSTRAINT fk_finance_animal FOREIGN KEY (animal_tag_no) REFERENCES graze.animal (tag_no)
);

CREATE INDEX idx_finance_transaction_type ON graze.finance (transaction_type);
CREATE INDEX idx_finance_category ON graze.finance (category);
CREATE INDEX idx_finance_animal_tag_no ON graze.finance (animal_tag_no);
CREATE INDEX idx_finance_date ON graze.finance (date);

