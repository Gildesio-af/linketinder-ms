package zg.acelera.dto.user_skill

import jakarta.validation.Validation
import spock.lang.Specification
import spock.lang.Unroll
import zg.acelera.domain.Skill
import zg.acelera.domain.SkillLevel
import zg.acelera.domain.UserSkill
import zg.acelera.domain.UserSkillId
import zg.acelera.dto.skill.SkillAssociationCreateDTO

class UserSkillDTOSpec extends Specification {

    def validator = Validation.buildDefaultValidatorFactory().validator

    def "create DTO validates valid values"() {
        given:
        SkillAssociationCreateDTO dto = new SkillAssociationCreateDTO(
                skillId: UUID.randomUUID(),
                level: SkillLevel.ADVANCED)

        expect:
        validator.validate(dto).empty
    }

    @Unroll
    def "create DTO rejects invalid #field"() {
        given:
        SkillAssociationCreateDTO dto = new SkillAssociationCreateDTO(skillId: skillId, level: level)

        expect:
        !validator.validate(dto).empty

        where:
        field    | skillId           | level
        "skill"  | null              | SkillLevel.BEGINNER
        "level"  | UUID.randomUUID() | null
    }

    def "update DTO validates a valid level"() {
        given:
        UserSkillUpdateDTO dto = new UserSkillUpdateDTO(level: SkillLevel.INTERMEDIATE)

        expect:
        validator.validate(dto).empty
    }

    def "update DTO rejects a null level"() {
        given:
        UserSkillUpdateDTO dto = new UserSkillUpdateDTO(level: null)

        expect:
        !validator.validate(dto).empty
    }

    def "response DTO is mapped from the domain"() {
        given:
        UUID userId = UUID.randomUUID()
        UUID skillId = UUID.randomUUID()
        UserSkill userSkill = new UserSkill(
                id: new UserSkillId(userId: userId, skillId: skillId),
                skill: new Skill(id: skillId, name: "Java"),
                level: SkillLevel.ADVANCED)

        when:
        UserSkillResponseDTO response = UserSkillResponseDTO.fromDomain(userSkill)

        then:
        response.userId == userId
        response.skillId == skillId
        response.skillName == "Java"
        response.level == SkillLevel.ADVANCED
    }
}
