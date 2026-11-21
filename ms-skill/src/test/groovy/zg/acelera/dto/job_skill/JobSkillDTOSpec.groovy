package zg.acelera.dto.job_skill

import jakarta.validation.Validation
import spock.lang.Specification
import spock.lang.Unroll
import zg.acelera.domain.JobSkill
import zg.acelera.domain.JobSkillId
import zg.acelera.domain.Skill
import zg.acelera.domain.SkillLevel
import zg.acelera.dto.skill.SkillAssociationCreateDTO

class JobSkillDTOSpec extends Specification {

    def validator = Validation.buildDefaultValidatorFactory().validator

    def "create DTO validates valid values"() {
        given:
        def dto = new SkillAssociationCreateDTO(
                skillId: UUID.randomUUID(),
                level: SkillLevel.ADVANCED)

        expect:
        validator.validate(dto).empty
    }

    @Unroll
    def "create DTO rejects invalid #field"() {
        given:
        def dto = new SkillAssociationCreateDTO(skillId: skillId, level: level)

        expect:
        !validator.validate(dto).empty

        where:
        field    | skillId           | level
        "skill"  | null              | SkillLevel.BEGINNER
        "level"  | UUID.randomUUID() | null
    }

    def "update DTO validates a valid level"() {
        given:
        def dto = new JobSkillUpdateDTO(level: SkillLevel.INTERMEDIATE)

        expect:
        validator.validate(dto).empty
    }

    def "update DTO rejects a null level"() {
        given:
        def dto = new JobSkillUpdateDTO(level: null)

        expect:
        !validator.validate(dto).empty
    }

    def "response DTO is mapped from the domain"() {
        given:
        UUID jobId = UUID.randomUUID()
        UUID skillId = UUID.randomUUID()
        def jobSkill = new JobSkill(
                id: new JobSkillId(jobId: jobId, skillId: skillId),
                skill: new Skill(id: skillId, name: "Java"),
                level: SkillLevel.ADVANCED)

        when:
        def response = JobSkillResponseDTO.fromDomain(jobSkill)

        then:
        response.jobId == jobId
        response.skillId == skillId
        response.skillName == "Java"
        response.level == SkillLevel.ADVANCED
    }
}
