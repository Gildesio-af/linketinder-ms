package zg.acelera.msnucleo.controller

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import spock.lang.Specification
import zg.acelera.msnucleo.domain.User
import zg.acelera.msnucleo.domain.UserRole
import zg.acelera.msnucleo.dto.job.JobCreateDTO
import zg.acelera.msnucleo.dto.job.JobResponseDTO
import zg.acelera.msnucleo.dto.job.JobUpdateDTO
import zg.acelera.msnucleo.infrastructure.security.CustomUserDetails
import zg.acelera.msnucleo.service.JobService

class JobControllerTest extends Specification {
    JobService jobService
    JobController controller

    def setup() {
        jobService = Mock()
        controller = new JobController(jobService)
    }

    def cleanup() {
        RequestContextHolder.resetRequestAttributes()
    }

    void "findById returns job"() {
        given:
        UUID id = UUID.randomUUID()
        JobResponseDTO response = new JobResponseDTO(id: id)
        jobService.findById(id) >> response

        when:
        def result = controller.findById(id)

        then:
        result.statusCode == HttpStatus.OK
        result.body == response
    }

    void "findAll returns job page"() {
        given:
        PageRequest pageable = PageRequest.of(0, 10)
        JobResponseDTO job = new JobResponseDTO(id: UUID.randomUUID())
        Page<JobResponseDTO> page = new PageImpl([job], pageable, 1)
        jobService.findAll(pageable) >> page

        when:
        def result = controller.findAll(pageable)

        then:
        result.statusCode == HttpStatus.OK
        result.body == page
    }

    void "findByCompanyId returns jobs for publisher"() {
        given:
        UUID companyId = UUID.randomUUID()
        PageRequest pageable = PageRequest.of(0, 10)
        Page<JobResponseDTO> page = new PageImpl([], pageable, 0)
        jobService.findByPublisherId(companyId, pageable) >> page

        when:
        def result = controller.findByCompanyId(companyId, pageable)

        then:
        result.statusCode == HttpStatus.OK
        result.body == page
    }

    void "save returns created response"() {
        given:
        UUID id = UUID.randomUUID()
        UUID addressId = UUID.randomUUID()
        UUID companyId = UUID.randomUUID()
        CustomUserDetails principal = new CustomUserDetails(new User(
                id: companyId,
                email: 'contato@acme.com',
                role: UserRole.COMPANY))
        JobCreateDTO dto = new JobCreateDTO(
                name: 'Java Developer',
                description: 'Backend development',
                addressId: addressId)
        JobResponseDTO saved = new JobResponseDTO(id: id)
        jobService.save({
            JobCreateDTO value -> value.publisherId == companyId
        }) >> saved
        MockHttpServletRequest request = new MockHttpServletRequest(
                'POST', '/jobs')
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request))

        when:
        def result = controller.save(principal, dto)

        then:
        result.statusCode == HttpStatus.CREATED
        result.body == saved
        result.headers.location != null
    }

    void "update returns updated job"() {
        given:
        UUID id = UUID.randomUUID()
        JobUpdateDTO dto = new JobUpdateDTO(name: 'Senior Java Developer')
        JobResponseDTO response = new JobResponseDTO(
                id: id, name: 'Senior Java Developer')
        jobService.update(id, dto) >> response

        when:
        def result = controller.update(id, dto)

        then:
        result.statusCode == HttpStatus.OK
        result.body == response
    }

    void "delete returns no content"() {
        given:
        UUID id = UUID.randomUUID()

        when:
        def result = controller.delete(id)

        then:
        result.statusCode == HttpStatus.NO_CONTENT
        1 * jobService.delete(id)
    }
}
