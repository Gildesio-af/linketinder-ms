package zg.acelera.service

import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import spock.lang.Specification
import spock.lang.Subject
import zg.acelera.domain.Skill
import zg.acelera.dto.skill.SkillCreateDTO
import zg.acelera.repositories.skill.SkillRepository
import zg.acelera.utils.exception.EntityNotFoundException

class SkillServiceSpec extends Specification {

    SkillRepository skillRepository = Mock()

    @Subject
    SkillService skillService = new SkillService(skillRepository)

    def "findById returns the skill when it exists"() {
        given:
        UUID id = UUID.randomUUID()
        Skill skill = new Skill(id: id, name: "Java")
        skillRepository.findById(id) >> Optional.of(skill)

        when:
        def response = skillService.findById(id)

        then:
        response.id == id
        response.name == "Java"
    }

    def "findById throws EntityNotFoundException when the skill does not exist"() {
        given:
        UUID id = UUID.randomUUID()
        skillRepository.findById(id) >> Optional.empty()

        when:
        skillService.findById(id)

        then:
        def exception = thrown(EntityNotFoundException)
        exception.message == "Skill not found with id: $id"
    }

    def "findAll returns a mapped page"() {
        given:
        def pageable = PageRequest.of(0, 10)
        def skills = [
                new Skill(id: UUID.randomUUID(), name: "Java"),
                new Skill(id: UUID.randomUUID(), name: "Groovy")
        ]
        skillRepository.findAll(pageable) >> new PageImpl<>(skills, pageable, skills.size())

        when:
        def response = skillService.findAll(pageable)

        then:
        response.content*.name == ["Java", "Groovy"]
        response.totalElements == 2
    }

    def "findByNameIgnoreCase returns the skill"() {
        given:
        Skill skill = new Skill(id: UUID.randomUUID(), name: "Java")
        skillRepository.findByNameIgnoreCase("java") >> Optional.of(skill)

        when:
        def response = skillService.findByNameIgnoreCase("java")

        then:
        response.id == skill.id
        response.name == "Java"
    }

    def "findByNameIgnoreCase throws EntityNotFoundException when the skill does not exist"() {
        given:
        skillRepository.findByNameIgnoreCase("Python") >> Optional.empty()

        when:
        skillService.findByNameIgnoreCase("Python")

        then:
        def exception = thrown(EntityNotFoundException)
        exception.message == "Skill not found with name: Python"
    }

    def "save trims the name, creates and returns the skill"() {
        given:
        SkillCreateDTO dto = new SkillCreateDTO(name: "  Java  ")
        Skill saved = new Skill(id: UUID.randomUUID(), name: "Java")
        skillRepository.findByNameIgnoreCase("Java") >> Optional.empty()
        skillRepository.save({ Skill skill ->
            skill.name == "Java"
        }) >> saved

        when:
        def response = skillService.save(dto)

        then:
        response.id == saved.id
        response.name == "Java"
    }

    def "save rejects a duplicate name regardless of casing"() {
        given:
        SkillCreateDTO dto = new SkillCreateDTO(name: "Java")
        Skill existing = new Skill(id: UUID.randomUUID(), name: "Java")
        skillRepository.findByNameIgnoreCase("Java") >> Optional.of(existing)

        when:
        skillService.save(dto)

        then:
        def exception = thrown(IllegalArgumentException)
        exception.message == "Skill with name 'Java' already exists"
        0 * skillRepository.save(_)
    }

    def "saveAll creates every skill"() {
        given:
        def java = new SkillCreateDTO(name: "  Java  ")
        def groovy = new SkillCreateDTO(name: "Groovy")
        def savedSkills = [
                new Skill(id: UUID.randomUUID(), name: "Java"),
                new Skill(id: UUID.randomUUID(), name: "Groovy")
        ]
        skillRepository.findByNameIgnoreCase("Java") >> Optional.empty()
        skillRepository.findByNameIgnoreCase("Groovy") >> Optional.empty()
        skillRepository.saveAll(_ as List<Skill>) >> savedSkills

        when:
        def response = skillService.saveAll([java, groovy])

        then:
        response*.name == ["Java", "Groovy"]
    }

    def "saveAll rejects an empty batch"() {
        when:
        skillService.saveAll([])

        then:
        IllegalArgumentException exception = thrown()
        exception.message == "At least one skill is required"
        0 * skillRepository._
    }

    def "saveAll rejects duplicate names regardless of casing"() {
        given:
        def java = new SkillCreateDTO(name: "Java")
        def javaAgain = new SkillCreateDTO(name: " java ")

        when:
        skillService.saveAll([java, javaAgain])

        then:
        IllegalArgumentException exception = thrown()
        exception.message == "Duplicate skill name in batch: Java"
        0 * skillRepository._
    }

    def "saveAll rejects an existing name"() {
        given:
        def java = new SkillCreateDTO(name: "Java")
        skillRepository.findByNameIgnoreCase("Java") >>
                Optional.of(new Skill(id: UUID.randomUUID(), name: "Java"))

        when:
        skillService.saveAll([java])

        then:
        IllegalArgumentException exception = thrown()
        exception.message == "Skill with name 'Java' already exists"
        0 * skillRepository.saveAll(_)
    }

    def "delete removes an existing skill"() {
        given:
        UUID id = UUID.randomUUID()
        skillRepository.existsById(id) >> true

        when:
        skillService.delete(id)

        then:
        1 * skillRepository.delete(id)
    }

    def "delete throws EntityNotFoundException when the skill does not exist"() {
        given:
        UUID id = UUID.randomUUID()
        skillRepository.existsById(id) >> false

        when:
        skillService.delete(id)

        then:
        def exception = thrown(EntityNotFoundException)
        exception.message == "Skill not found with id: $id"
        0 * skillRepository.delete(_)
    }
}
