-- Seed default users matching the Keycloak realm export
INSERT INTO graze.users (username, email, first_name, last_name, email_verified)
VALUES
  ('AdminUser',   'admin.user@graze.example.com',   'Admin',        'User', TRUE),
  ('ManagerUser', 'manager.user@graze.example.com', 'Manager',      'User', TRUE),
  ('VetUser',     'vet.user@graze.example.com',     'Veterinarian', 'User', TRUE),
  ('WorkerUser',  'worker.user@graze.example.com',  'Worker',       'User', TRUE),
  ('OwnerUser',   'owner.user@graze.example.com',   'Owner',        'User', TRUE);
