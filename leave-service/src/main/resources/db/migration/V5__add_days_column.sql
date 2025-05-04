-- Add days column to leaves table
ALTER TABLE leaves ADD COLUMN IF NOT EXISTS days INTEGER;

-- Update existing records to calculate days based on start and end dates
UPDATE leaves 
SET days = EXTRACT(DAY FROM (end_date - start_date)) + 1
WHERE days IS NULL;

-- Add NOT NULL constraint
ALTER TABLE leaves ALTER COLUMN days SET NOT NULL; 