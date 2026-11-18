package zg.acelera.dto.skill

import jakarta.validation.Validation
import spock.lang.Specification
import spock.lang.Unroll
import zg.acelera.domain.Skill

class SkillCreateDTOSpec extends Specification {

    def validator = Validation.buildDefaultValidatorFactory().validator

    def "toDomain trims the skill name"() {
        given:
        def dto = new SkillCreateDTO(name: "  Java  ")

        when:
        Skill skill = dto.toDomain()

        then:
        skill.name == "Java"
    }

    def "response DTO is mapped from the domain"() {
        given:
        UUID id = UUID.randomUUID()
        Skill skill = new Skill(id: id, name: "Java")

        when:
        def response = SkillResponseDTO.fromDomain(skill)

        then:
        response.id == id
        response.name == "Java"
    }

    @Unroll
    def "name '#name' fails validation"() {
        given:
        def dto = new SkillCreateDTO(name: name)

        expect:
        !validator.validate(dto).empty

        where:
        name << [null, "", "   ", "\t"]
    }

    def "a valid name passes validation"() {
        given:
        def dto = new SkillCreateDTO(name: "Java")

        expect:
        validator.validate(dto).empty
    }
}
