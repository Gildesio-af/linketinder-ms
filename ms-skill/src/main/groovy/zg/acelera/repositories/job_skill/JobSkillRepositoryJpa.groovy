package zg.acelera.repositories.job_skill

import org.springframework.data.jpa.repository.JpaRepository
import zg.acelera.domain.JobSkill
import zg.acelera.domain.JobSkillId

interface JobSkillRepositoryJpa extends JpaRepository<JobSkill, JobSkillId> {
    List<JobSkill> findByJobId(UUID jobId)
}