package zg.acelera.dto.user_skill

import jakarta.validation.constraints.NotNull
import zg.acelera.domain.SkillLevel

record UserSkillUpdateDTO(
        @NotNull(message = "Skill level cannot be null")
        SkillLevel level
) {
}
