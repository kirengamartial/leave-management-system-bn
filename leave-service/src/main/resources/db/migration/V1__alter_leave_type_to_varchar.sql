-- Flyway migration: Alter leave_type column to VARCHAR
ALTER TABLE leaves
  ALTER COLUMN leave_type TYPE VARCHAR
  USING leave_type::varchar; 