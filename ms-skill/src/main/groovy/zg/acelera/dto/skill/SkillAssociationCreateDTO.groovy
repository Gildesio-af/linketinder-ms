package zg.acelera.dto.skill

import jakarta.validation.constraints.NotNull
import zg.acelera.domain.SkillLevel

class SkillAssociationCreateDTO {
    @NotNull(message = "Skill id cannot be null")
    private UUID skillId

    @NotNull(message = "Skill level cannot be null")
    private SkillLevel level

    UUID getSkillId() {
        return skillId
    }

    void setSkillId(UUID skillId) {
        this.skillId = skillId
    }

    UUID skillId() {
        return skillId
    }

    SkillLevel getLevel() {
        return level
    }

    void setLevel(SkillLevel level) {
        this.level = level
    }

    SkillLevel level() {
        return level
    }
}
