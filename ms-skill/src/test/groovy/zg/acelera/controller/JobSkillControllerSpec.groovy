package zg.acelera.controller

import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import spock.lang.Specification
import spock.lang.Subject
import zg.acelera.domain.SkillLevel
import zg.acelera.dto.job_skill.JobSkillCreateDTO
import zg.acelera.dto.job_skill.JobSkillResponseDTO
import zg.acelera.service.JobSkillService

class JobSkillControllerSpec extends Specification {

    JobSkillService jobSkillService = Mock()
    Authentication authentication = Mock()

    @Subject
    JobSkillController controller = new JobSkillController(jobSkillService)

    def "saveAll returns created with the authenticated company id"() {
        given:
        UUID jobId = UUID.randomUUID()
        UUID companyId = UUID.randomUUID()
        UUID skillId = UUID.randomUUID()
        List<JobSkillCreateDTO> skills = [
                new JobSkillCreateDTO(skillId: skillId, level: SkillLevel.ADVANCED)
        ]
        List<JobSkillResponseDTO> saved = [
                new JobSkillResponseDTO(
                        jobId: jobId,
                        skillId: skillId,
                        skillName: "Java",
                        level: SkillLevel.ADVANCED)
        ]
        authentication.principal >> companyId

        when:
        def response = controller.saveAll(jobId, skills, authentication)

        then:
        response.statusCode == HttpStatus.CREATED
        response.body == saved
        1 * jobSkillService.saveAll(jobId, companyId, skills) >> saved
    }
}
