package zg.acelera.service

import spock.lang.Specification
import spock.lang.Subject
import zg.acelera.domain.JobSkill
import zg.acelera.domain.JobSkillId
import zg.acelera.domain.Skill
import zg.acelera.domain.SkillLevel
import zg.acelera.dto.skill.SkillAssociationCreateDTO
import zg.acelera.dto.job_skill.JobSkillUpdateDTO
import zg.acelera.repositories.job_skill.JobSkillRepository
import zg.acelera.repositories.skill.SkillRepository
import zg.acelera.utils.exception.EntityNotFoundException

class JobSkillServiceSpec extends Specification {

    JobSkillRepository jobSkillRepository = Mock()
    SkillRepository skillRepository = Mock()

    @Subject
    JobSkillService service = new JobSkillService(jobSkillRepository, skillRepository)

    UUID jobId
    UUID skillId
    UUID companyId

    def setup() {
        jobId = UUID.randomUUID()
        skillId = UUID.randomUUID()
        companyId = UUID.randomUUID()
    }

    def "findByJobId returns the mapped skills"() {
        given:
        def jobSkill = createJobSkill(SkillLevel.ADVANCED)
        jobSkillRepository.findByJobId(jobId) >> [jobSkill]

        when:
        def response = service.findByJobId(jobId)

        then:
        response.size() == 1
        response[0].jobId == jobId
        response[0].skillId == skillId
        response[0].skillName == "Java"
        response[0].level == SkillLevel.ADVANCED
    }

    def "findById returns the requested job skill"() {
        given:
        def jobSkill = createJobSkill(SkillLevel.INTERMEDIATE)
        jobSkillRepository.findByJobIdAndSkillId(_ as JobSkillId) >> Optional.of(jobSkill)

        when:
        def response = service.findById(jobId, skillId)

        then:
        response.jobId == jobId
        response.skillId == skillId
        response.level == SkillLevel.INTERMEDIATE
    }

    def "findById throws when the job skill does not exist"() {
        given:
        jobSkillRepository.findByJobIdAndSkillId(_ as JobSkillId) >> Optional.empty()

        when:
        service.findById(jobId, skillId)

        then:
        def exception = thrown(EntityNotFoundException)
        exception.message == "Job skill not found"
    }

    def "save creates and returns a job skill"() {
        given:
        def dto = new SkillAssociationCreateDTO(skillId: skillId, level: SkillLevel.BEGINNER)
        def skill = new Skill(id: skillId, name: "Java")
        jobSkillRepository.findByJobIdAndSkillId(_ as JobSkillId) >> Optional.empty()
        skillRepository.findById(skillId) >> Optional.of(skill)
        jobSkillRepository.save(_ as JobSkill) >> { JobSkill saved -> saved }

        when:
        def response = service.save(jobId, companyId, dto)

        then:
        response.jobId == jobId
        response.skillId == skillId
        response.skillName == "Java"
        response.level == SkillLevel.BEGINNER
    }

    def "save rejects an already associated skill"() {
        given:
        def dto = new SkillAssociationCreateDTO(skillId: skillId, level: SkillLevel.ADVANCED)
        jobSkillRepository.findByJobIdAndSkillId(_ as JobSkillId) >>
                Optional.of(createJobSkill(SkillLevel.BEGINNER))

        when:
        service.save(jobId, companyId, dto)

        then:
        def exception = thrown(zg.acelera.utils.exception.DuplicateResourceException)
        exception.message == "Skill is already associated with job"
        0 * skillRepository.findById(_)
        0 * jobSkillRepository.save(_)
    }

    def "save throws when the skill does not exist"() {
        given:
        def dto = new SkillAssociationCreateDTO(skillId: skillId, level: SkillLevel.BEGINNER)
        jobSkillRepository.findByJobIdAndSkillId(_ as JobSkillId) >> Optional.empty()
        skillRepository.findById(skillId) >> Optional.empty()

        when:
        service.save(jobId, companyId, dto)

        then:
        def exception = thrown(EntityNotFoundException)
        exception.message == "Skill not found with id: $skillId"
        0 * jobSkillRepository.save(_)
    }

    def "saveAll creates every skill and assigns the company id"() {
        given:
        UUID secondSkillId = UUID.randomUUID()
        def java = new SkillAssociationCreateDTO(skillId: skillId, level: SkillLevel.BEGINNER)
        def groovy = new SkillAssociationCreateDTO(skillId: secondSkillId, level: SkillLevel.ADVANCED)
        def javaSkill = new Skill(id: skillId, name: "Java")
        def groovySkill = new Skill(id: secondSkillId, name: "Groovy")
        def savedJobSkills = [
                new JobSkill(
                        id: new JobSkillId(jobId: jobId, skillId: skillId),
                        companyId: companyId,
                        skill: javaSkill,
                        level: SkillLevel.BEGINNER),
                new JobSkill(
                        id: new JobSkillId(jobId: jobId, skillId: secondSkillId),
                        companyId: companyId,
                        skill: groovySkill,
                        level: SkillLevel.ADVANCED)
        ]
        skillRepository.findAllById([skillId, secondSkillId]) >> [javaSkill, groovySkill]
        jobSkillRepository.findByJobId(jobId) >> []
        jobSkillRepository.saveAll(_ as List<JobSkill>) >> savedJobSkills

        when:
        def response = service.saveAll(jobId, companyId, [java, groovy])

        then:
        response*.jobId == [jobId, jobId]
        response*.skillId == [skillId, secondSkillId]
        response*.level == [SkillLevel.BEGINNER, SkillLevel.ADVANCED]
    }

    def "saveAll rejects an empty batch"() {
        when:
        service.saveAll(jobId, UUID.randomUUID(), [])

        then:
        IllegalArgumentException exception = thrown()
        exception.message == "At least one job skill is required"
        0 * jobSkillRepository._
        0 * skillRepository._
    }

    def "saveAll rejects duplicate skill ids"() {
        given:
        def dto = new SkillAssociationCreateDTO(skillId: skillId, level: SkillLevel.BEGINNER)

        when:
        service.saveAll(jobId, UUID.randomUUID(), [dto, dto])

        then:
        zg.acelera.utils.exception.DuplicateResourceException exception = thrown()
        exception.message == "Duplicate skill id in batch: $skillId"
        0 * jobSkillRepository._
        0 * skillRepository._
    }

    def "saveAll is atomic when one skill does not exist"() {
        given:
        UUID missingSkillId = UUID.randomUUID()
        def existing = new SkillAssociationCreateDTO(skillId: skillId, level: SkillLevel.BEGINNER)
        def missing = new SkillAssociationCreateDTO(skillId: missingSkillId, level: SkillLevel.ADVANCED)
        skillRepository.findAllById(_ as List<UUID>) >>
                [new Skill(id: skillId, name: "Java")]
        jobSkillRepository.findByJobId(jobId) >> []

        when:
        service.saveAll(jobId, UUID.randomUUID(), [existing, missing])

        then:
        thrown(EntityNotFoundException)
        0 * jobSkillRepository.saveAll(_)
    }

    def "update changes the skill level"() {
        given:
        def jobSkill = createJobSkill(SkillLevel.BEGINNER)
        def dto = new JobSkillUpdateDTO(level: SkillLevel.ADVANCED)
        jobSkillRepository.findByJobIdAndSkillId(_ as JobSkillId) >> Optional.of(jobSkill)
        jobSkillRepository.save(_ as JobSkill) >> { JobSkill saved -> saved }

        when:
        def response = service.update(jobId, skillId, companyId, dto)

        then:
        response.level == SkillLevel.ADVANCED
        jobSkill.level == SkillLevel.ADVANCED
    }

    def "update throws when the job skill does not exist"() {
        given:
        def dto = new JobSkillUpdateDTO(level: SkillLevel.ADVANCED)
        jobSkillRepository.findByJobIdAndSkillId(_ as JobSkillId) >> Optional.empty()

        when:
        service.update(jobId, skillId, companyId, dto)

        then:
        thrown(EntityNotFoundException)
        0 * jobSkillRepository.save(_)
    }

    def "delete removes an existing job skill"() {
        given:
        jobSkillRepository.findByJobIdAndSkillId(_ as JobSkillId) >>
                Optional.of(createJobSkill(SkillLevel.BEGINNER))

        when:
        service.delete(jobId, skillId, companyId)

        then:
        1 * jobSkillRepository.delete({ JobSkillId id ->
            id.jobId == jobId && id.skillId == skillId
        })
    }

    def "delete throws when the job skill does not exist"() {
        given:
        jobSkillRepository.findByJobIdAndSkillId(_ as JobSkillId) >> Optional.empty()

        when:
        service.delete(jobId, skillId, companyId)

        then:
        def exception = thrown(EntityNotFoundException)
        exception.message == "Job skill not found"
        0 * jobSkillRepository.delete(_)
    }

    private JobSkill createJobSkill(SkillLevel level) {
        return new JobSkill(
                id: new JobSkillId(jobId: jobId, skillId: skillId),
                companyId: companyId,
                skill: new Skill(id: skillId, name: "Java"),
                level: level)
    }
}
