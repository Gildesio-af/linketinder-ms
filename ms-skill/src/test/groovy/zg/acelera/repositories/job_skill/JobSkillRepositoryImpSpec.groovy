package zg.acelera.repositories.job_skill

import spock.lang.Specification
import spock.lang.Subject
import zg.acelera.domain.JobSkill
import zg.acelera.domain.JobSkillId
import zg.acelera.domain.Skill
import zg.acelera.domain.SkillLevel

class JobSkillRepositoryImpSpec extends Specification {

    JobSkillRepositoryJpa jpaRepository = Mock()

    @Subject
    JobSkillRepositoryImp repository = new JobSkillRepositoryImp(jpaRepository)

    def "findByJobId delegates to the JPA repository"() {
        given:
        UUID jobId = UUID.randomUUID()
        def jobSkill = createJobSkill(jobId)
        jpaRepository.findAllByIdJobId(jobId) >> [jobSkill]

        expect:
        repository.findByJobId(jobId) == [jobSkill]
    }

    def "findByJobIdAndSkillId delegates to the JPA repository"() {
        given:
        def id = new JobSkillId(
                jobId: UUID.randomUUID(),
                skillId: UUID.randomUUID())
        def jobSkill = createJobSkill(id.jobId)
        jpaRepository.findById(id) >> Optional.of(jobSkill)

        expect:
        repository.findByJobIdAndSkillId(id) == Optional.of(jobSkill)
    }

    def "save delegates to the JPA repository"() {
        given:
        def jobSkill = createJobSkill(UUID.randomUUID())
        jpaRepository.save(jobSkill) >> jobSkill

        expect:
        repository.save(jobSkill) == jobSkill
    }

    def "saveAll delegates one batch to the JPA repository"() {
        given:
        def jobSkills = [createJobSkill(UUID.randomUUID()), createJobSkill(UUID.randomUUID())]
        jpaRepository.saveAll(_ as Iterable) >> jobSkills

        when:
        def result = repository.saveAll(jobSkills)

        then:
        result == jobSkills
        1 * jpaRepository.saveAll(_ as Iterable) >> jobSkills
    }

    def "delete delegates to the JPA repository"() {
        given:
        def id = new JobSkillId(
                jobId: UUID.randomUUID(),
                skillId: UUID.randomUUID())

        when:
        repository.delete(id)

        then:
        1 * jpaRepository.deleteById(id)
    }

    private static JobSkill createJobSkill(UUID jobId) {
        UUID skillId = UUID.randomUUID()
        return new JobSkill(
                id: new JobSkillId(jobId: jobId, skillId: skillId),
                skill: new Skill(id: skillId, name: "Java"),
                level: SkillLevel.BEGINNER)
    }
}
