package zg.acelera.dto.skill

import jakarta.validation.constraints.NotBlank
import zg.acelera.domain.Skill

class SkillCreateDTO {
    @NotBlank(message = "Skill name cannot be blank")
    private String name

    String getName() {
        return name
    }

    void setName(String name) {
        this.name = name
    }

    String name() {
        return name
    }

    Skill toDomain() {
        return new Skill(name: name.trim())
    }
}