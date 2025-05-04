-- First update any null days values
UPDATE leaves 
SET days = EXTRACT(DAY FROM (end_date - start_date)) + 1
WHERE days IS NULL;

-- Then add NOT NULL constraint
ALTER TABLE leaves ALTER COLUMN days SET NOT NULL; 