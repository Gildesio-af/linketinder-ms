package zg.acelera.service

import spock.lang.Specification
import spock.lang.Subject
import zg.acelera.domain.JobSkill
import zg.acelera.domain.JobSkillId
import zg.acelera.domain.Skill
import zg.acelera.domain.SkillLevel
import zg.acelera.dto.job_skill.JobSkillCreateDTO
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

    def setup() {
        jobId = UUID.randomUUID()
        skillId = UUID.randomUUID()
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
        def dto = new JobSkillCreateDTO(skillId: skillId, level: SkillLevel.BEGINNER)
        def skill = new Skill(id: skillId, name: "Java")
        jobSkillRepository.findByJobIdAndSkillId(_ as JobSkillId) >> Optional.empty()
        skillRepository.findById(skillId) >> Optional.of(skill)
        jobSkillRepository.save(_ as JobSkill) >> { JobSkill saved -> saved }

        when:
        def response = service.save(jobId, dto)

        then:
        response.jobId == jobId
        response.skillId == skillId
        response.skillName == "Java"
        response.level == SkillLevel.BEGINNER
    }

    def "save rejects an already associated skill"() {
        given:
        def dto = new JobSkillCreateDTO(skillId: skillId, level: SkillLevel.ADVANCED)
        jobSkillRepository.findByJobIdAndSkillId(_ as JobSkillId) >>
                Optional.of(createJobSkill(SkillLevel.BEGINNER))

        when:
        service.save(jobId, dto)

        then:
        def exception = thrown(IllegalArgumentException)
        exception.message == "Skill is already associated with job"
        0 * skillRepository.findById(_)
        0 * jobSkillRepository.save(_)
    }

    def "save throws when the skill does not exist"() {
        given:
        def dto = new JobSkillCreateDTO(skillId: skillId, level: SkillLevel.BEGINNER)
        jobSkillRepository.findByJobIdAndSkillId(_ as JobSkillId) >> Optional.empty()
        skillRepository.findById(skillId) >> Optional.empty()

        when:
        service.save(jobId, dto)

        then:
        def exception = thrown(EntityNotFoundException)
        exception.message == "Skill not found with id: $skillId"
        0 * jobSkillRepository.save(_)
    }

    def "update changes the skill level"() {
        given:
        def jobSkill = createJobSkill(SkillLevel.BEGINNER)
        def dto = new JobSkillUpdateDTO(level: SkillLevel.ADVANCED)
        jobSkillRepository.findByJobIdAndSkillId(_ as JobSkillId) >> Optional.of(jobSkill)
        jobSkillRepository.save(_ as JobSkill) >> { JobSkill saved -> saved }

        when:
        def response = service.update(jobId, skillId, dto)

        then:
        response.level == SkillLevel.ADVANCED
        jobSkill.level == SkillLevel.ADVANCED
    }

    def "update throws when the job skill does not exist"() {
        given:
        def dto = new JobSkillUpdateDTO(level: SkillLevel.ADVANCED)
        jobSkillRepository.findByJobIdAndSkillId(_ as JobSkillId) >> Optional.empty()

        when:
        service.update(jobId, skillId, dto)

        then:
        thrown(EntityNotFoundException)
        0 * jobSkillRepository.save(_)
    }

    def "delete removes an existing job skill"() {
        given:
        jobSkillRepository.findByJobIdAndSkillId(_ as JobSkillId) >>
                Optional.of(createJobSkill(SkillLevel.BEGINNER))

        when:
        service.delete(jobId, skillId)

        then:
        1 * jobSkillRepository.delete({ JobSkillId id ->
            id.jobId == jobId && id.skillId == skillId
        })
    }

    def "delete throws when the job skill does not exist"() {
        given:
        jobSkillRepository.findByJobIdAndSkillId(_ as JobSkillId) >> Optional.empty()

        when:
        service.delete(jobId, skillId)

        then:
        def exception = thrown(EntityNotFoundException)
        exception.message == "Job skill not found"
        0 * jobSkillRepository.delete(_)
    }

    private JobSkill createJobSkill(SkillLevel level) {
        return new JobSkill(
                id: new JobSkillId(jobId: jobId, skillId: skillId),
                skill: new Skill(id: skillId, name: "Java"),
                level: level)
    }
}
