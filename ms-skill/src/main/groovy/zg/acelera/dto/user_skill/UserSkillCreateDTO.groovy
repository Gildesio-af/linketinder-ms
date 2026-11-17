package zg.acelera.dto.user_skill

import jakarta.validation.constraints.NotBlank
import org.hibernate.validator.constraints.Length
import zg.acelera.domain.SkillLevel

record UserSkillCreateDTO(
        @Length(min = 36, max = 36, message = "Skill id must be a valid UUID")
        UUID skillId,

        @NotBlank(message = "Skill level cannot be blank")
        SkillLevel level
) {
}
