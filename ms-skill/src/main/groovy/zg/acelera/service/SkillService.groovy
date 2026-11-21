package zg.acelera.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import zg.acelera.domain.Skill
import zg.acelera.dto.skill.SkillCreateDTO
import zg.acelera.dto.skill.SkillResponseDTO
import zg.acelera.repositories.skill.SkillRepository
import zg.acelera.utils.exception.EntityNotFoundException
import zg.acelera.utils.exception.DuplicateResourceException

@Service
class SkillService {

    private final SkillRepository skillRepository

    SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository
    }

    @Transactional(readOnly = true)
    SkillResponseDTO findById(UUID id) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow { new EntityNotFoundException("Skill not found with id: $id") }
        return SkillResponseDTO.fromDomain(skill)
    }

    @Transactional(readOnly = true)
    Page<SkillResponseDTO> findAll(Pageable pageable) {
        return skillRepository.findAll(pageable).map { SkillResponseDTO.fromDomain(it) }
    }

    @Transactional(readOnly = true)
    SkillResponseDTO findByNameIgnoreCase(String name) {
        return skillRepository.findByNameIgnoreCase(name)
                .map { SkillResponseDTO.fromDomain(it) }
                .orElseThrow { new EntityNotFoundException("Skill not found with name: $name") }
    }

    @Transactional
    SkillResponseDTO save(SkillCreateDTO skillCreateDTO) {
        Skill skill = skillCreateDTO.toDomain()
        validateUniqueName(skill.name)

        Skill savedSkill = skillRepository.save(skill)
        return SkillResponseDTO.fromDomain(savedSkill)
    }

    @Transactional
    List<SkillResponseDTO> saveAll(List<SkillCreateDTO> skillCreateDTOs) {
        validateBatch(skillCreateDTOs)

        List<Skill> skills = skillCreateDTOs.collect { it.toDomain() }
        validateUniqueNames(skills)
        skills.each { validateUniqueName(it.name) }

        List<Skill> savedSkills = skillRepository.saveAll(skills)
        return savedSkills.collect { SkillResponseDTO.fromDomain(it) }
    }

    @Transactional
    void delete(UUID id) {
        if (!skillRepository.existsById(id))
            throw new EntityNotFoundException("Skill not found with id: $id")

        skillRepository.delete(id)
    }

    private static void validateBatch(List<SkillCreateDTO> skillCreateDTOs) {
        if (skillCreateDTOs == null || skillCreateDTOs.isEmpty())
            throw new IllegalArgumentException("At least one skill is required")
    }

    private static void validateUniqueNames(List<Skill> skills) {
        Map<String, String> namesByNormalizedName = new LinkedHashMap<>()
        for (Skill skill : skills) {
            String normalizedName = skill.name.toLowerCase(Locale.ROOT)
            if (namesByNormalizedName.containsKey(normalizedName))
                throw new DuplicateResourceException(
                        "Duplicate skill name in batch: ${namesByNormalizedName[normalizedName]}")
            namesByNormalizedName[normalizedName] = skill.name
        }
    }

    private void validateUniqueName(String name) {
        if (skillRepository.findByNameIgnoreCase(name).isPresent())
            throw new DuplicateResourceException("Skill with name '${name}' already exists")
    }
}
