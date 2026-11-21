package zg.acelera.controller

import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Specification
import zg.acelera.domain.Skill
import zg.acelera.domain.SkillLevel
import zg.acelera.domain.UserSkill
import zg.acelera.domain.UserSkillId
import zg.acelera.repositories.skill.SkillRepository
import zg.acelera.repositories.user_skill.UserSkillRepository
import zg.acelera.service.UserSkillService
import zg.acelera.utils.exception.handler.GlobalExceptionHandler

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

class UserSkillBatchSpec extends Specification {
    UserSkillRepository repository = Mock()
    SkillRepository skillRepository = Stub()
    MockMvc mvc = MockMvcBuilders.standaloneSetup(
            new UserSkillController(new UserSkillService(repository, skillRepository)))
            .setControllerAdvice(new GlobalExceptionHandler())
            .build()

    def "groups skills for multiple users and includes users without skills"() {
        given:
        UUID first = UUID.randomUUID()
        UUID second = UUID.randomUUID()
        UUID withoutSkills = UUID.randomUUID()
        UserSkill java = association(first, "Java")
        UserSkill groovy = association(second, "Groovy")

        when:
        def response = mvc.perform(get("/users/skills/batch").param("userIds", "${first},${second},${withoutSkills}".toString()))

        then:
        response.andExpect(status().isOk())
                .andExpect(jsonPath("\$['${first}'][0].skillName").value("Java"))
                .andExpect(jsonPath("\$['${second}'][0].skillName").value("Groovy"))
                .andExpect(jsonPath("\$['${withoutSkills}']").isEmpty())

        and:
        1 * repository.findByUserIds([first, second, withoutSkills] as Set) >> [groovy, java]
        0 * repository._
    }

    def "returns an empty object for an empty request without querying the repository"() {
        when:
        def response = mvc.perform(get("/users/skills/batch").param("userIds", ""))

        then:
        response.andExpect(status().isOk())
                .andExpect(content().string("{}"))

        and:
        0 * repository._
    }

    def "queries repeated IDs once and returns one group"() {
        given:
        UUID userId = UUID.randomUUID()

        when:
        def response = mvc.perform(get("/users/skills/batch").param("userIds", userId.toString(), userId.toString()))

        then:
        response.andExpect(status().isOk())
                .andExpect(content().string("{\"${userId}\":[]}"))

        and:
        1 * repository.findByUserIds([userId] as Set) >> []
        0 * repository._
    }

    def "rejects more than 100 IDs before deduplication"() {
        given:
        String userId = UUID.randomUUID().toString()

        when:
        def response = mvc.perform(get("/users/skills/batch").param("userIds", ([userId] * 101).join(",")))

        then:
        response.andExpect(status().isBadRequest())
                .andExpect(jsonPath('$.error').value("At most 100 userIds are allowed per request"))

        and:
        0 * repository._
    }

    private static UserSkill association(UUID userId, String name) {
        UUID skillId = UUID.randomUUID()
        return new UserSkill(id: new UserSkillId(userId: userId, skillId: skillId),
                skill: new Skill(id: skillId, name: name), level: SkillLevel.ADVANCED)
    }
}
