package zg.acelera.dto.job_skill

import zg.acelera.domain.JobSkill
import zg.acelera.domain.SkillLevel

class JobSkillResponseDTO {
    UUID jobId
    UUID skillId
    String skillName
    SkillLevel level

    static JobSkillResponseDTO fromDomain(JobSkill jobSkill) {
        return new JobSkillResponseDTO(
                jobId: jobSkill.id.jobId,
                skillId: jobSkill.id.skillId,
                skillName: jobSkill.skill.name,
                level: jobSkill.level
        )
    }
}
