-- Table: users
CREATE TABLE graze.users
(
  id             UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
  keycloak_id    VARCHAR(255),
  username       VARCHAR(255) NOT NULL UNIQUE,
  email          VARCHAR(255) NOT NULL UNIQUE,
  first_name     VARCHAR(255),
  last_name      VARCHAR(255),
  email_verified BOOLEAN      NOT NULL DEFAULT FALSE,
  created_at     TIMESTAMP    NOT NULL DEFAULT now()
);
