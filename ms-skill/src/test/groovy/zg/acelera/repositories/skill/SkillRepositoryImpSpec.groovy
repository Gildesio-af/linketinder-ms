package zg.acelera.repositories.skill

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import spock.lang.Specification
import spock.lang.Subject
import zg.acelera.domain.Skill

class SkillRepositoryImpSpec extends Specification {

    SkillRepositoryJpa jpaRepository = Mock()

    @Subject
    SkillRepositoryImp repository = new SkillRepositoryImp(jpaRepository)

    def "findById delegates to the JPA repository"() {
        given:
        UUID id = UUID.randomUUID()
        Skill skill = new Skill(id: id, name: "Java")
        jpaRepository.findById(id) >> Optional.of(skill)

        expect:
        repository.findById(id) == Optional.of(skill)
    }

    def "findAll delegates to the JPA repository"() {
        given:
        PageRequest pageable = PageRequest.of(0, 10)
        Page<Skill> skills = new PageImpl<>([new Skill(id: UUID.randomUUID(), name: "Java")])
        jpaRepository.findAll(pageable) >> skills

        expect:
        repository.findAll(pageable) == skills
    }

    def "findByNameIgnoreCase delegates to the JPA repository"() {
        given:
        Skill skill = new Skill(id: UUID.randomUUID(), name: "Java")
        jpaRepository.findByNameIgnoreCase("java") >> Optional.of(skill)

        expect:
        repository.findByNameIgnoreCase("java") == Optional.of(skill)
    }

    def "save delegates to the JPA repository"() {
        given:
        Skill skill = new Skill(name: "Java")
        jpaRepository.save(skill) >> skill

        expect:
        repository.save(skill) == skill
    }

    def "saveAll delegates one batch to the JPA repository"() {
        given:
        def skills = [new Skill(name: "Java"), new Skill(name: "Groovy")]
        jpaRepository.saveAll(_ as Iterable) >> skills

        when:
        def result = repository.saveAll(skills)

        then:
        result == skills
        1 * jpaRepository.saveAll(_ as Iterable) >> skills
    }

    def "existsById delegates to the JPA repository"() {
        given:
        UUID id = UUID.randomUUID()
        jpaRepository.existsById(id) >> true

        expect:
        repository.existsById(id)
    }

    def "delete delegates to the JPA repository"() {
        given:
        UUID id = UUID.randomUUID()

        when:
        repository.delete(id)

        then:
        1 * jpaRepository.deleteById(id)
    }
}
