package zg.acelera.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import zg.acelera.domain.Skill
import zg.acelera.domain.UserSkill
import zg.acelera.domain.UserSkillId
import zg.acelera.dto.skill.SkillAssociationCreateDTO
import zg.acelera.dto.user_skill.UserSkillResponseDTO
import zg.acelera.dto.user_skill.UserSkillUpdateDTO
import zg.acelera.repositories.skill.SkillRepository
import zg.acelera.repositories.user_skill.UserSkillRepository
import zg.acelera.utils.exception.EntityNotFoundException
import zg.acelera.utils.exception.DuplicateResourceException

@Service
class UserSkillService {
    private static final int MAX_BATCH_USER_IDS = 100

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
    Map<UUID, List<UserSkillResponseDTO>> findByUserIds(List<UUID> userIds) {
        if (userIds.size() > MAX_BATCH_USER_IDS)
            throw new IllegalArgumentException("At most ${MAX_BATCH_USER_IDS} userIds are allowed per request")
        if (userIds.contains(null))
            throw new IllegalArgumentException("Each userIds entry must be a UUID")
        if (userIds.isEmpty())
            return [:]

        Map<UUID, List<UserSkillResponseDTO>> skillsByUserId = [:]
        Set<UUID> uniqueUserIds = new LinkedHashSet<>(userIds)
        uniqueUserIds.each { skillsByUserId[it] = [] }
        userSkillRepository.findByUserIds(uniqueUserIds).each { UserSkill userSkill ->
            skillsByUserId[userSkill.id.userId].add(UserSkillResponseDTO.fromDomain(userSkill))
        }
        return skillsByUserId
    }

    @Transactional(readOnly = true)
    UserSkillResponseDTO findById(UUID userId, UUID skillId) {
        UserSkill userSkill = findEntity(userId, skillId)
        return UserSkillResponseDTO.fromDomain(userSkill)
    }

    @Transactional
    UserSkillResponseDTO save(UUID userId, SkillAssociationCreateDTO userSkillCreateDTO) {
        UserSkillId id = new UserSkillId(userId: userId, skillId: userSkillCreateDTO.skillId())
        if (userSkillRepository.findByUserIdAndSkillId(id).isPresent())
            throw new DuplicateResourceException("Skill is already associated with user")

        Skill skill = skillRepository.findById(userSkillCreateDTO.skillId())
                .orElseThrow { new EntityNotFoundException(
                        "Skill not found with id: ${userSkillCreateDTO.skillId()}") }
        UserSkill userSkill = createUserSkill(id, userSkillCreateDTO, skill)

        return UserSkillResponseDTO.fromDomain(userSkillRepository.save(userSkill))
    }

    @Transactional
    List<UserSkillResponseDTO> saveAll(UUID userId, List<SkillAssociationCreateDTO> skills) {
        validateBatch(skills)

        Set<UUID> skillIds = extractUniqueSkillIds(skills)
        Map<UUID, Skill> existingSkills = loadExistingSkills(skillIds)
        validateNoExistingUserSkills(userId, skillIds)

        List<UserSkill> userSkills = buildUserSkills(userId, skills, existingSkills)
        List<UserSkill> savedUserSkills = userSkillRepository.saveAll(userSkills)

        return savedUserSkills.collect { UserSkillResponseDTO.fromDomain(it) }
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

    private static void validateBatch(List<SkillAssociationCreateDTO> skills) {
        if (skills == null || skills.isEmpty())
            throw new IllegalArgumentException("At least one user skill is required")
    }

    private static Set<UUID> extractUniqueSkillIds(List<SkillAssociationCreateDTO> skills) {
        Set<UUID> skillIds = new LinkedHashSet<>()
        for (SkillAssociationCreateDTO skill : skills) {
            if (skill.skillId() == null)
                throw new IllegalArgumentException("Skill id cannot be null")
            if (!skillIds.add(skill.skillId()))
                throw new DuplicateResourceException("Duplicate skill id in batch: ${skill.skillId()}")
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

    private void validateNoExistingUserSkills(UUID userId, Set<UUID> skillIds) {
        List<UserSkill> existingUserSkills = userSkillRepository.findByUserId(userId) ?: []
        Set<UUID> existingSkillIds = existingUserSkills.collect { it.id.skillId } as Set

        for (UUID skillId : skillIds) {
            if (existingSkillIds.contains(skillId))
                throw new DuplicateResourceException("Skill is already associated with user")
        }
    }

    private static List<UserSkill> buildUserSkills(UUID userId, List<SkillAssociationCreateDTO> skills,
                                                   Map<UUID, Skill> skillsById) {
        return skills.collect { SkillAssociationCreateDTO skill ->
            UserSkillId id = new UserSkillId(userId: userId, skillId: skill.skillId())
            createUserSkill(id, skill, skillsById[skill.skillId()])
        }
    }

    private static UserSkill createUserSkill(UserSkillId id, SkillAssociationCreateDTO dto, Skill skill) {
        new UserSkill(id: id, skill: skill, level: dto.level())
    }
}
