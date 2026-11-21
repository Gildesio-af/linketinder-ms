ALTER TABLE jobs_skill
    ADD COLUMN IF NOT EXISTS company_id UUID;
