package zg.acelera.dto.user_skill

import jakarta.validation.constraints.NotNull
import zg.acelera.domain.SkillLevel

class UserSkillUpdateDTO {
    @NotNull(message = "Skill level cannot be null")
    private SkillLevel level

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
