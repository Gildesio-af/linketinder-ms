package zg.acelera.controller

import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import spock.lang.Specification
import spock.lang.Subject
import zg.acelera.domain.SkillLevel
import zg.acelera.dto.skill.SkillAssociationCreateDTO
import zg.acelera.dto.user_skill.UserSkillResponseDTO
import zg.acelera.service.UserSkillService

class UserSkillControllerSpec extends Specification {

    UserSkillService userSkillService = Mock()
    Authentication authentication = Mock()

    @Subject
    UserSkillController controller = new UserSkillController(userSkillService)

    def "saveAll returns created with the authenticated user id"() {
        given:
        UUID userId = UUID.randomUUID()
        UUID skillId = UUID.randomUUID()
        List<SkillAssociationCreateDTO> skills = [
                new SkillAssociationCreateDTO(skillId: skillId, level: SkillLevel.ADVANCED)
        ]
        List<UserSkillResponseDTO> saved = [
                new UserSkillResponseDTO(
                        userId: userId,
                        skillId: skillId,
                        skillName: "Java",
                        level: SkillLevel.ADVANCED)
        ]
        authentication.principal >> userId

        when:
        def response = controller.saveAll(skills, authentication)

        then:
        response.statusCode == HttpStatus.CREATED
        response.body == saved
        1 * userSkillService.saveAll(userId, skills) >> saved
    }
}
