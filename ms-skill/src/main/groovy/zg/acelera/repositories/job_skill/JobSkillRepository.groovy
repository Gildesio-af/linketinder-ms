package zg.acelera.repositories.job_skill

import zg.acelera.domain.JobSkill
import zg.acelera.domain.JobSkillId

interface JobSkillRepository {
    List<JobSkill> findByJobId(UUID jobId)
    Optional<JobSkill> findByJobIdAndSkillId(JobSkillId id)
    JobSkill save(JobSkill jobSkill)
    void delete(JobSkillId id)
}
