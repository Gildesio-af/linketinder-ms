package zg.acelera.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import zg.acelera.domain.JobSkill
import zg.acelera.domain.JobSkillId
import zg.acelera.domain.Skill
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
    JobSkillResponseDTO findById(UUID jobId, UUID skillId) {
        JobSkill jobSkill = findEntity(jobId, skillId)
        return JobSkillResponseDTO.fromDomain(jobSkill)
    }

    @Transactional(readOnly = true)
    List<JobSkillResponseDTO> findByJobId(UUID jobId) {
        return jobSkillRepository.findByJobId(jobId)
                .collect { JobSkillResponseDTO.fromDomain(it) }
    }

    @Transactional
    JobSkillResponseDTO save(UUID jobId, UUID companyId, JobSkillCreateDTO jobSkillCreateDTO) {
        JobSkillId id = new JobSkillId(jobId: jobId, skillId: jobSkillCreateDTO.skillId())
        if (jobSkillRepository.findByJobIdAndSkillId(id).isPresent())
            throw new IllegalArgumentException("Skill is already associated with job")

        Skill skill = skillRepository.findById(jobSkillCreateDTO.skillId())
                .orElseThrow { new EntityNotFoundException(
                        "Skill not found with id: ${jobSkillCreateDTO.skillId()}") }
        JobSkill jobSkill = createSkill(id, companyId, jobSkillCreateDTO, skill)

        return JobSkillResponseDTO.fromDomain(jobSkillRepository.save(jobSkill))
    }

    @Transactional
    List<JobSkillResponseDTO> saveAll(UUID jobId, UUID companyId, List<JobSkillCreateDTO> skills) {
        validateBatch(skills)

        Set<UUID> skillIds = extractUniqueSkillIds(skills)
        Map<UUID, Skill> existingSkills = loadExistingSkills(skillIds)
        validateNoExistingJobSkills(jobId, skillIds)

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

    private static void validateBatch(List<JobSkillCreateDTO> skills) {
        if (skills == null || skills.isEmpty())
            throw new IllegalArgumentException("At least one job skill is required")
    }

    private static Set<UUID> extractUniqueSkillIds(List<JobSkillCreateDTO> skills) {
        Set<UUID> skillIds = new LinkedHashSet<>()
        for (JobSkillCreateDTO skill : skills) {
            if (skill.skillId() == null)
                throw new IllegalArgumentException("Skill id cannot be null")
            if (!skillIds.add(skill.skillId()))
                throw new IllegalArgumentException("Duplicate skill id in batch: ${skill.skillId()}")
        }
        return skillIds
    }

    private Map<UUID, Skill> loadExistingSkills(Set<UUID> skillIds) {
        List<Skill> foundSkills = skillRepository.findAllById(skillIds.toList()) ?: []
        Map<UUID, Skill> skillsById = foundSkills.collectEntries { [it.id, it] }

        for (UUID skillId : skillIds) {
            if (!skillsById.containsKey(skillId))
                throw new EntityNotFoundException("Skill not found with id: ${skillId}")
        }
        return skillsById
    }

    private void validateNoExistingJobSkills(UUID jobId, Set<UUID> skillIds) {
        List<JobSkill> existingJobSkills = jobSkillRepository.findByJobId(jobId) ?: []
        Set<UUID> existingSkillIds = existingJobSkills.collect { it.id.skillId } as Set

        for (UUID skillId : skillIds) {
            if (existingSkillIds.contains(skillId))
                throw new IllegalArgumentException("Skill is already associated with job")
        }
    }

    private static List<JobSkill> buildJobSkills(UUID jobId, UUID companyId,
                                                 List<JobSkillCreateDTO> skills,
                                                 Map<UUID, Skill> skillsById) {
        return skills.collect { JobSkillCreateDTO skill ->
            JobSkillId id = new JobSkillId(jobId: jobId, skillId: skill.skillId())
            createSkill(id, companyId, skill, skillsById[skill.skillId()])
        }
    }

    private static JobSkill createSkill(JobSkillId id, UUID companyId,
                                        JobSkillCreateDTO jobSkillCreateDTO, Skill skill) {
        new JobSkill(
                id: id,
                companyId: companyId,
                skill: skill,
                level: jobSkillCreateDTO.level())
    }
}
