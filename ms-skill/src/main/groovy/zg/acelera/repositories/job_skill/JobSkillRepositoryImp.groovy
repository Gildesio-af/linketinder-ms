package zg.acelera.repositories.job_skill

import org.springframework.stereotype.Repository
import zg.acelera.domain.JobSkill
import zg.acelera.domain.JobSkillId

@Repository
class JobSkillRepositoryImp implements JobSkillRepository {
    private final JobSkillRepositoryJpa jobSkillRepositoryJpa

    JobSkillRepositoryImp(JobSkillRepositoryJpa jobSkillRepositoryJpa) {
        this.jobSkillRepositoryJpa = jobSkillRepositoryJpa
    }

    @Override
    List<JobSkill> findByJobId(UUID jobId) {
        return jobSkillRepositoryJpa.findByJobId(jobId)
    }

    @Override
    Optional<JobSkill> findByJobIdAndSkillId(JobSkillId JobSkillId) {
        return jobSkillRepositoryJpa.findById(JobSkillId)
    }

    @Override
    JobSkill save(JobSkill jobSkill) {
        return jobSkillRepositoryJpa.save(jobSkill)
    }

    @Override
    void delete(JobSkillId JobSkillId) {
        jobSkillRepositoryJpa.deleteById(JobSkillId)
    }
}
