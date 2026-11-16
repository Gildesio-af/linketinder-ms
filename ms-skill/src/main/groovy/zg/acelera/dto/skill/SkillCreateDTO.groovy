package zg.acelera.dto.skill

import jakarta.validation.constraints.NotBlank
import zg.acelera.domain.Skill

record SkillCreateDTO(
        @NotBlank(message = "Skill name cannot be blank")
        String name
) {
    Skill toDomain() {
        return new Skill(name: this.name.trim())
    }
}