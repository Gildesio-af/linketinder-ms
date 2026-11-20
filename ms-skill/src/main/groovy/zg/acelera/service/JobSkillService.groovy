package zg.acelera.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import zg.acelera.domain.JobSkill
import zg.acelera.domain.JobSkillId
import zg.acelera.dto.job_skill.JobSkillCreateDTO
import zg.acelera.dto.job_skill.JobSkillResponseDTO
import zg.acelera.dto.job_skill.JobSkillUpdateDTO
import zg.acelera.repositories.job_skill.JobSkillRepository
import zg.acelera.repositories.skill.SkillRepository
import zg.acelera.utils.exception.EntityNotFoundException

@Service
class JobSkillService {
    private final JobSkillRepository jobSkillRepository
    private final SkillRepository skillRepository

    JobSkillService(JobSkillRepository jobSkillRepository, SkillRepository skillRepository) {
        this.jobSkillRepository = jobSkillRepository
        this.skillRepository = skillRepository
    }

    @Transactional(readOnly = true)
    List<JobSkillResponseDTO> findByJobId(UUID jobId) {
        return jobSkillRepository.findByJobId(jobId)
                .collect { JobSkillResponseDTO.fromDomain(it) }
    }

    @Transactional(readOnly = true)
    JobSkillResponseDTO findById(UUID jobId, UUID skillId) {
        JobSkill jobSkill = findEntity(jobId, skillId)
        return JobSkillResponseDTO.fromDomain(jobSkill)
    }

    @Transactional
    JobSkillResponseDTO save(UUID jobId, JobSkillCreateDTO jobSkillCreateDTO) {
        return save(jobId, null, jobSkillCreateDTO)
    }

    @Transactional
    JobSkillResponseDTO save(UUID jobId, UUID companyId, JobSkillCreateDTO jobSkillCreateDTO) {
        JobSkillId id = new JobSkillId(jobId: jobId, skillId: jobSkillCreateDTO.skillId())
        if (jobSkillRepository.findByJobIdAndSkillId(id).isPresent())
            throw new IllegalArgumentException("Skill is already associated with job")

        JobSkill jobSkill = new JobSkill(
                id: id,
                companyId: companyId,
                skill: skillRepository.findById(jobSkillCreateDTO.skillId())
                        .orElseThrow { new EntityNotFoundException(
                                "Skill not found with id: ${jobSkillCreateDTO.skillId()}") },
                level: jobSkillCreateDTO.level())

        return JobSkillResponseDTO.fromDomain(jobSkillRepository.save(jobSkill))
    }

    @Transactional
    JobSkillResponseDTO update(UUID jobId, UUID skillId, JobSkillUpdateDTO jobSkillUpdateDTO) {
        return update(jobId, skillId, null, jobSkillUpdateDTO)
    }

    @Transactional
    JobSkillResponseDTO update(UUID jobId, UUID skillId, UUID companyId, JobSkillUpdateDTO jobSkillUpdateDTO) {
        JobSkill jobSkill = findEntity(jobId, skillId)
        if (companyId != null && jobSkill.companyId != companyId)
            throw new EntityNotFoundException("Job skill not found")
        jobSkill.level = jobSkillUpdateDTO.level()
        return JobSkillResponseDTO.fromDomain(jobSkillRepository.save(jobSkill))
    }

    @Transactional
    void delete(UUID jobId, UUID skillId) {
        delete(jobId, skillId, null)
    }

    @Transactional
    void delete(UUID jobId, UUID skillId, UUID companyId) {
        JobSkillId id = new JobSkillId(jobId: jobId, skillId: skillId)
        Optional<JobSkill> existing = jobSkillRepository.findByJobIdAndSkillId(id)
        if (!existing.isPresent())
            throw new EntityNotFoundException("Job skill not found")
        if (companyId != null && existing.get().companyId != companyId)
            throw new EntityNotFoundException("Job skill not found")

        jobSkillRepository.delete(id)
    }

    private JobSkill findEntity(UUID jobId, UUID skillId) {
        JobSkillId id = new JobSkillId(jobId: jobId, skillId: skillId)
        return jobSkillRepository.findByJobIdAndSkillId(id)
                .orElseThrow { new EntityNotFoundException("Job skill not found") }
    }
}
