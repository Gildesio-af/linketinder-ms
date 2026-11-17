package zg.acelera.dto.user_skill

import zg.acelera.domain.UserSkill
import zg.acelera.domain.SkillLevel

class UserSkillResponseDTO {
    UUID userId
    UUID skillId
    String skillName
    SkillLevel level

    static UserSkillResponseDTO fromDomain(UserSkill userSkill) {
        return new UserSkillResponseDTO(
                userId: userSkill.id.userId,
                skillId: userSkill.id.skillId,
                skillName: userSkill.skill.name,
                level: userSkill.level
        )
    }
}
