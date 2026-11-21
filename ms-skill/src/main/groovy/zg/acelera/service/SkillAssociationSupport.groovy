package zg.acelera.service

import groovy.transform.PackageScope
import zg.acelera.domain.Skill
import zg.acelera.dto.skill.SkillAssociationCreateDTO
import zg.acelera.repositories.skill.SkillRepository
import zg.acelera.utils.exception.DuplicateResourceException
import zg.acelera.utils.exception.EntityNotFoundException

@PackageScope
final class SkillAssociationSupport {
    private static final int MAX_LOOKUP_IDS = 50

    private SkillAssociationSupport() {}

    static Set<UUID> validateLookupIds(List<UUID> ids, String parameterName) {
        if (ids.size() > MAX_LOOKUP_IDS)
            throw new IllegalArgumentException("At most ${MAX_LOOKUP_IDS} ${parameterName} are allowed per request")
        if (ids.contains(null))
            throw new IllegalArgumentException("Each ${parameterName} entry must be a UUID")
        return new LinkedHashSet<>(ids)
    }

    static Skill loadSkill(SkillRepository repository, UUID skillId) {
        return repository.findById(skillId).orElseThrow { skillNotFound(skillId) }
    }

    static Map<UUID, Skill> loadBatch(SkillRepository repository, List<SkillAssociationCreateDTO> skills,
                                     String emptyBatchMessage) {
        if (skills == null || skills.isEmpty())
            throw new IllegalArgumentException(emptyBatchMessage)

        Set<UUID> skillIds = extractUniqueSkillIds(skills)
        List<Skill> foundSkills = repository.findAllById(skillIds.toList()) ?: []
        Map<UUID, Skill> skillsById = foundSkills.collectEntries { [it.id, it] }

        for (UUID skillId : skillIds) {
            if (!skillsById.containsKey(skillId))
                throw skillNotFound(skillId)
        }
        return skillsById
    }

    static void rejectExistingAssociations(Set<UUID> requestedIds, Collection<UUID> existingIds,
                                           String duplicateMessage) {
        if (existingIds.any { requestedIds.contains(it) })
            throw new DuplicateResourceException(duplicateMessage)
    }

    private static Set<UUID> extractUniqueSkillIds(List<SkillAssociationCreateDTO> skills) {
        Set<UUID> skillIds = new LinkedHashSet<>()
        for (SkillAssociationCreateDTO skill : skills) {
            UUID skillId = skill.skillId()
            if (skillId == null)
                throw new IllegalArgumentException("Skill id cannot be null")
            if (!skillIds.add(skillId))
                throw new DuplicateResourceException("Duplicate skill id in batch: ${skillId}")
        }
        return skillIds
    }

    private static EntityNotFoundException skillNotFound(UUID skillId) {
        return new EntityNotFoundException("Skill not found with id: ${skillId}")
    }
}
