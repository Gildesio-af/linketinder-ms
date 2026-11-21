package zg.acelera.controller

import org.springframework.http.HttpStatus
import spock.lang.Specification
import spock.lang.Subject
import zg.acelera.dto.skill.SkillCreateDTO
import zg.acelera.dto.skill.SkillResponseDTO
import zg.acelera.service.SkillService

class SkillControllerSpec extends Specification {

    SkillService skillService = Mock()

    @Subject
    SkillController controller = new SkillController(skillService)

    def "saveAll returns created"() {
        given:
        UUID javaId = UUID.randomUUID()
        List<SkillCreateDTO> skills = [new SkillCreateDTO(name: "Java")]
        List<SkillResponseDTO> saved = [
                new SkillResponseDTO(id: javaId, name: "Java")
        ]

        when:
        def response = controller.saveAll(skills)

        then:
        response.statusCode == HttpStatus.CREATED
        response.body == saved
        1 * skillService.saveAll(skills) >> saved
    }
}
