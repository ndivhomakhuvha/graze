# Flyway Configurations
Naming flyway migrations goes like this : ```V<version>__<description>.sql```

## Here’s what a healthy workflow looks like:

Every schema change → new migration file </br>
Migration reviewed in PR </br>
CI runs app startup (Flyway runs automatically)
Deployment applies migrations safely
