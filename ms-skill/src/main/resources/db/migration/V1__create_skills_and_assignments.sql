CREATE TABLE skills (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE users_skill (
    user_id UUID NOT NULL,
    skill_id UUID NOT NULL,
    level VARCHAR(15) NOT NULL CHECK (level IN ('BEGINNER', 'INTERMEDIATE', 'ADVANCED')),
    PRIMARY KEY (user_id, skill_id),
    CONSTRAINT fk_users_skill_skill FOREIGN KEY (skill_id) REFERENCES skills(id) ON DELETE CASCADE
);

CREATE TABLE jobs_skill (
    job_id UUID NOT NULL,
    skill_id UUID NOT NULL,
    level VARCHAR(15) DEFAULT 'BEGINNER' CHECK (level IN ('BEGINNER', 'INTERMEDIATE', 'ADVANCED')),
    PRIMARY KEY (job_id, skill_id),
    CONSTRAINT fk_jobs_skill_skill FOREIGN KEY (skill_id) REFERENCES skills(id) ON DELETE CASCADE
);

CREATE INDEX idx_users_skill_user_id ON users_skill(user_id);
CREATE INDEX idx_jobs_skill_job_id ON jobs_skill(job_id);