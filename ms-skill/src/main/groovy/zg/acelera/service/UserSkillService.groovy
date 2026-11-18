package zg.acelera.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import zg.acelera.domain.UserSkill
import zg.acelera.domain.UserSkillId
import zg.acelera.dto.user_skill.UserSkillCreateDTO
import zg.acelera.dto.user_skill.UserSkillResponseDTO
import zg.acelera.dto.user_skill.UserSkillUpdateDTO
import zg.acelera.repositories.skill.SkillRepository
import zg.acelera.repositories.user_skill.UserSkillRepository
import zg.acelera.utils.exception.EntityNotFoundException

@Service
class UserSkillService {
    private final UserSkillRepository userSkillRepository
    private final SkillRepository skillRepository

    UserSkillService(UserSkillRepository userSkillRepository, SkillRepository skillRepository) {
        this.userSkillRepository = userSkillRepository
        this.skillRepository = skillRepository
    }

    @Transactional(readOnly = true)
    List<UserSkillResponseDTO> findByUserId(UUID userId) {
        return userSkillRepository.findByUserId(userId)
                .collect { UserSkillResponseDTO.fromDomain(it) }
    }

    @Transactional(readOnly = true)
    UserSkillResponseDTO findById(UUID userId, UUID skillId) {
        UserSkill userSkill = findEntity(userId, skillId)
        return UserSkillResponseDTO.fromDomain(userSkill)
    }

    @Transactional
    UserSkillResponseDTO save(UUID userId, UserSkillCreateDTO userSkillCreateDTO) {
        UserSkillId id = new UserSkillId(userId: userId, skillId: userSkillCreateDTO.skillId())
        if (userSkillRepository.findByUserIdAndSkillId(id).isPresent())
            throw new IllegalArgumentException("Skill is already associated with user")

        UserSkill userSkill = new UserSkill(
                id: id,
                skill: skillRepository.findById(userSkillCreateDTO.skillId())
                        .orElseThrow { new EntityNotFoundException(
                                "Skill not found with id: ${userSkillCreateDTO.skillId()}") },
                level: userSkillCreateDTO.level())

        return UserSkillResponseDTO.fromDomain(userSkillRepository.save(userSkill))
    }

    @Transactional
    UserSkillResponseDTO update(UUID userId, UUID skillId, UserSkillUpdateDTO userSkillUpdateDTO) {
        UserSkill userSkill = findEntity(userId, skillId)
        userSkill.level = userSkillUpdateDTO.level()
        return UserSkillResponseDTO.fromDomain(userSkillRepository.save(userSkill))
    }

    @Transactional
    void delete(UUID userId, UUID skillId) {
        UserSkillId id = new UserSkillId(userId: userId, skillId: skillId)
        if (!userSkillRepository.findByUserIdAndSkillId(id).isPresent())
            throw new EntityNotFoundException("User skill not found")

        userSkillRepository.delete(id)
    }

    private UserSkill findEntity(UUID userId, UUID skillId) {
        UserSkillId id = new UserSkillId(userId: userId, skillId: skillId)
        return userSkillRepository.findByUserIdAndSkillId(id)
                .orElseThrow { new EntityNotFoundException("User skill not found") }
    }
}
