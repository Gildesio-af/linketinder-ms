package zg.acelera.repositories.job_skill

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import zg.acelera.domain.JobSkill
import zg.acelera.domain.JobSkillId

interface JobSkillRepositoryJpa extends JpaRepository<JobSkill, JobSkillId> {
    List<JobSkill> findAllByIdJobId(UUID jobId)

    @Query("select js from JobSkill js join fetch js.skill where js.id.jobId in (:jobIds)")
    List<JobSkill> findAllByJobIds(@Param("jobIds") Set<UUID> jobIds)
}
