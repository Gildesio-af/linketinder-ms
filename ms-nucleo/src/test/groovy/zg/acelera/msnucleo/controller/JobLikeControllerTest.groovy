package zg.acelera.msnucleo.controller

import org.springframework.http.HttpStatus
import spock.lang.Specification
import zg.acelera.msnucleo.domain.User
import zg.acelera.msnucleo.domain.UserRole
import zg.acelera.msnucleo.dto.job.JobLikeResponseDTO
import zg.acelera.msnucleo.infrastructure.security.CustomUserDetails
import zg.acelera.msnucleo.service.JobLikeService

class JobLikeControllerTest extends Specification {
    JobLikeService jobLikeService
    JobLikeController controller

    def setup() {
        jobLikeService = Mock()
        controller = new JobLikeController(jobLikeService)
    }

    void "save creates job like using authenticated candidate id"() {
        given:
        UUID candidateId = UUID.randomUUID()
        UUID jobId = UUID.randomUUID()
        CustomUserDetails principal = new CustomUserDetails(new User(
                id: candidateId,
                email: 'ana@example.com',
                role: UserRole.CANDIDATE))
        JobLikeResponseDTO response = new JobLikeResponseDTO(
                candidateId: candidateId,
                jobId: jobId)
        jobLikeService.save(candidateId, jobId) >> response

        when:
        def result = controller.save(principal, jobId)

        then:
        result.statusCode == HttpStatus.CREATED
        result.body == response
    }

    void "delete returns no content"() {
        given:
        UUID candidateId = UUID.randomUUID()
        UUID jobId = UUID.randomUUID()
        CustomUserDetails principal = new CustomUserDetails(new User(
                id: candidateId,
                email: 'ana@example.com',
                role: UserRole.CANDIDATE))

        when:
        def result = controller.delete(principal, jobId)

        then:
        result.statusCode == HttpStatus.NO_CONTENT
        1 * jobLikeService.delete(candidateId, jobId)
    }
}
