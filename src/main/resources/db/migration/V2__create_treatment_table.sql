-- Table: treatment
CREATE TABLE graze.treatment
(
  id                    BIGSERIAL PRIMARY KEY,
  name                  VARCHAR(255) NOT NULL,
  default_interval_days INT
);
