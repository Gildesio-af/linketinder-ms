package zg.acelera.dto

import zg.acelera.domain.Skill

class SkillResponseDTO {
    UUID id
    String name

    public static SkillResponseDTO fromDomain(Skill skill) {
        return new SkillResponseDTO(
            id: skill.id,
            name: skill.name
        )
    }
}
