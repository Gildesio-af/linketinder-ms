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
        if (skillRepository.findByNameIgnoreCase(skill.name).isPresent())
            throw new IllegalArgumentException("Skill with name '${skill.name}' already exists")

        Skill savedSkill = skillRepository.save(skill)
        return SkillResponseDTO.fromDomain(savedSkill)
    }

    @Transactional
    void delete(UUID id) {
        if (!skillRepository.existsById(id))
            throw new EntityNotFoundException("Skill not found with id: $id")

        skillRepository.delete(id)
    }
}