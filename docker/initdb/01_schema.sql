-- Create application schema and set defaults for the connected user.
-- This runs once on fresh container init (when data dir is empty).

CREATE SCHEMA IF NOT EXISTS graze;

-- Ensure the app user prefers the app schema.
ALTER ROLE graze_user SET search_path = graze, public;

-- Also set it for the current database as a fallback.
ALTER DATABASE graze SET search_path = graze, public;

