package zg.acelera.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import zg.acelera.domain.JobSkill
import zg.acelera.domain.JobSkillId
import zg.acelera.domain.Skill
import zg.acelera.dto.skill.SkillAssociationCreateDTO
import zg.acelera.dto.job_skill.JobSkillResponseDTO
import zg.acelera.dto.job_skill.JobSkillUpdateDTO
import zg.acelera.repositories.job_skill.JobSkillRepository
import zg.acelera.repositories.skill.SkillRepository
import zg.acelera.utils.exception.EntityNotFoundException
import zg.acelera.utils.exception.DuplicateResourceException

@Service
class JobSkillService {
    private final JobSkillRepository jobSkillRepository
    private final SkillRepository skillRepository

    JobSkillService(JobSkillRepository jobSkillRepository, SkillRepository skillRepository) {
        this.jobSkillRepository = jobSkillRepository
        this.skillRepository = skillRepository
    }

    @Transactional(readOnly = true)
    JobSkillResponseDTO findById(UUID jobId, UUID skillId) {
        JobSkill jobSkill = findEntity(jobId, skillId)
        return JobSkillResponseDTO.fromDomain(jobSkill)
    }

    @Transactional(readOnly = true)
    List<JobSkillResponseDTO> findByJobId(UUID jobId) {
        return jobSkillRepository.findByJobId(jobId)
                .collect { JobSkillResponseDTO.fromDomain(it) }
    }

    @Transactional(readOnly = true)
    Map<UUID, List<JobSkillResponseDTO>> findByJobIds(List<UUID> jobIds) {
        Set<UUID> uniqueJobIds = SkillAssociationSupport.validateLookupIds(jobIds, "jobIds")
        if (uniqueJobIds.isEmpty())
            return [:]

        Map<UUID, List<JobSkillResponseDTO>> skillsByJobId = [:]
        uniqueJobIds.each { skillsByJobId[it] = [] }
        jobSkillRepository.findByJobIds(uniqueJobIds).each { JobSkill jobSkill ->
            skillsByJobId[jobSkill.id.jobId].add(JobSkillResponseDTO.fromDomain(jobSkill))
        }
        return skillsByJobId
    }

    @Transactional
    JobSkillResponseDTO save(UUID jobId, UUID companyId, SkillAssociationCreateDTO jobSkillCreateDTO) {
        JobSkillId id = new JobSkillId(jobId: jobId, skillId: jobSkillCreateDTO.skillId())
        if (jobSkillRepository.findByJobIdAndSkillId(id).isPresent())
            throw new DuplicateResourceException("Skill is already associated with job")

        Skill skill = SkillAssociationSupport.loadSkill(skillRepository, jobSkillCreateDTO.skillId())
        JobSkill jobSkill = createSkill(id, companyId, jobSkillCreateDTO, skill)

        return JobSkillResponseDTO.fromDomain(jobSkillRepository.save(jobSkill))
    }

    @Transactional
    List<JobSkillResponseDTO> saveAll(UUID jobId, UUID companyId, List<SkillAssociationCreateDTO> skills) {
        Map<UUID, Skill> existingSkills = SkillAssociationSupport.loadBatch(
                skillRepository, skills, "At least one job skill is required")
        List<JobSkill> existingAssociations = jobSkillRepository.findByJobId(jobId) ?: []
        SkillAssociationSupport.rejectExistingAssociations(
                existingSkills.keySet(), existingAssociations.collect { it.id.skillId },
                "Skill is already associated with job")

        List<JobSkill> jobSkills = buildJobSkills(jobId, companyId, skills, existingSkills)
        List<JobSkill> savedJobSkills = jobSkillRepository.saveAll(jobSkills)

        return savedJobSkills.collect { JobSkillResponseDTO.fromDomain(it) }
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

    private static List<JobSkill> buildJobSkills(UUID jobId, UUID companyId,
                                                 List<SkillAssociationCreateDTO> skills,
                                                 Map<UUID, Skill> skillsById) {
        return skills.collect { SkillAssociationCreateDTO skill ->
            JobSkillId id = new JobSkillId(jobId: jobId, skillId: skill.skillId())
            createSkill(id, companyId, skill, skillsById[skill.skillId()])
        }
    }

    private static JobSkill createSkill(JobSkillId id, UUID companyId,
                                        SkillAssociationCreateDTO jobSkillCreateDTO, Skill skill) {
        new JobSkill(
                id: id,
                companyId: companyId,
                skill: skill,
                level: jobSkillCreateDTO.level())
    }
}
