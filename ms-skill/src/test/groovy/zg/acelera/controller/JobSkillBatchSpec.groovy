package zg.acelera.controller

import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Specification
import zg.acelera.domain.Skill
import zg.acelera.domain.SkillLevel
import zg.acelera.domain.JobSkill
import zg.acelera.domain.JobSkillId
import zg.acelera.repositories.skill.SkillRepository
import zg.acelera.repositories.job_skill.JobSkillRepository
import zg.acelera.service.JobSkillService
import zg.acelera.utils.exception.handler.GlobalExceptionHandler

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

class JobSkillBatchSpec extends Specification {
    JobSkillRepository repository = Mock()
    SkillRepository skillRepository = Stub()
    MockMvc mvc = MockMvcBuilders.standaloneSetup(
            new JobSkillController(new JobSkillService(repository, skillRepository)))
            .setControllerAdvice(new GlobalExceptionHandler())
            .build()

    def "groups skills for multiple jobs and includes jobs without skills"() {
        given:
        UUID first = UUID.randomUUID()
        UUID second = UUID.randomUUID()
        UUID withoutSkills = UUID.randomUUID()
        JobSkill java = association(first, "Java")
        JobSkill groovy = association(second, "Groovy")

        when:
        def response = mvc.perform(get("/jobs/skills/batch").param("jobIds", "${first},${second},${withoutSkills}".toString()))

        then:
        response.andExpect(status().isOk())
                .andExpect(jsonPath("\$['${first}'][0].skillName").value("Java"))
                .andExpect(jsonPath("\$['${second}'][0].skillName").value("Groovy"))
                .andExpect(jsonPath("\$['${withoutSkills}']").isEmpty())

        and:
        1 * repository.findByJobIds([first, second, withoutSkills] as Set) >> [groovy, java]
        0 * repository._
    }

    def "returns an empty object for an empty request without querying the repository"() {
        when:
        def response = mvc.perform(get("/jobs/skills/batch").param("jobIds", ""))

        then:
        response.andExpect(status().isOk())
                .andExpect(content().string("{}"))

        and:
        0 * repository._
    }

    def "queries repeated IDs once and returns one group"() {
        given:
        UUID jobId = UUID.randomUUID()

        when:
        def response = mvc.perform(get("/jobs/skills/batch").param("jobIds", jobId.toString(), jobId.toString()))

        then:
        response.andExpect(status().isOk())
                .andExpect(content().string("{\"${jobId}\":[]}"))

        and:
        1 * repository.findByJobIds([jobId] as Set) >> []
        0 * repository._
    }

    def "rejects more than 100 IDs before deduplication"() {
        given:
        String jobId = UUID.randomUUID().toString()

        when:
        def response = mvc.perform(get("/jobs/skills/batch").param("jobIds", ([jobId] * 101).join(",")))

        then:
        response.andExpect(status().isBadRequest())
                .andExpect(jsonPath('$.error').value("At most 100 jobIds are allowed per request"))

        and:
        0 * repository._
    }

    private static JobSkill association(UUID jobId, String name) {
        UUID skillId = UUID.randomUUID()
        return new JobSkill(id: new JobSkillId(jobId: jobId, skillId: skillId),
                skill: new Skill(id: skillId, name: name), level: SkillLevel.ADVANCED)
    }
}
