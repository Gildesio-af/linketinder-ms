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
import zg.acelera.msnucleo.dto.candidate.CandidateCreateDTO
import zg.acelera.msnucleo.dto.candidate.CandidateMatchResponseDTO
import zg.acelera.msnucleo.dto.candidate.CandidateResponseDTO
import zg.acelera.msnucleo.dto.candidate.CandidateUpdateDTO
import zg.acelera.msnucleo.dto.candidate.CandidateDataCreateDTO
import zg.acelera.msnucleo.dto.user.UserCreateDTO
import zg.acelera.msnucleo.infrastructure.security.CustomUserDetails
import zg.acelera.msnucleo.service.CandidateService

import java.time.LocalDate

class CandidateControllerTest extends Specification {
    CandidateService candidateService
    CandidateController controller

    def setup() {
        candidateService = Mock()
        controller = new CandidateController(candidateService)
    }

    def cleanup() {
        RequestContextHolder.resetRequestAttributes()
    }

    void "findMatchedCandidate returns response with matched candidate"() {
        given:
        UUID id = UUID.randomUUID()
        CandidateMatchResponseDTO response = new CandidateMatchResponseDTO(id: id)
        candidateService.findMatchedCandidate(id) >> response

        when:
        def result = controller.findMatchedCandidate(id)

        then:
        result.statusCode == HttpStatus.OK
        result.body == response
    }

    void "findAll returns candidate page"() {
        given:
        PageRequest pageable = PageRequest.of(0, 10)
        CandidateResponseDTO candidate = new CandidateResponseDTO(id: UUID.randomUUID())
        Page<CandidateResponseDTO> page = new PageImpl([candidate], pageable, 1)
        candidateService.findAll(pageable) >> page

        when:
        def result = controller.findAll(pageable)

        then:
        result.statusCode == HttpStatus.OK
        result.body == page
    }

    void "save returns created response"() {
        given:
        UUID id = UUID.randomUUID()
        CandidateCreateDTO dto = new CandidateCreateDTO(
                user: new UserCreateDTO(
                        name: 'Ana',
                        email: 'ana@example.com',
                        password: 'secret1',
                        description: 'Developer'),
                candidate: new CandidateDataCreateDTO(
                        cpf: '12345678900',
                        lastName: 'Silva',
                        birthDate: LocalDate.of(1990, 5, 10)))
        CandidateResponseDTO saved = new CandidateResponseDTO(id: id)
        candidateService.save(dto) >> saved
        MockHttpServletRequest request = new MockHttpServletRequest(
                'POST', '/candidates')
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request))

        when:
        def result = controller.save(dto)

        then:
        result.statusCode == HttpStatus.CREATED
        result.body == saved
        result.headers.location != null
    }

    void "update returns updated candidate"() {
        given:
        UUID id = UUID.randomUUID()
        CandidateUpdateDTO dto = new CandidateUpdateDTO(lastName: 'Santos')
        CandidateResponseDTO response = new CandidateResponseDTO(id: id, lastName: 'Santos')
        candidateService.update(id, dto) >> response

        when:
        def result = controller.update(id, dto)

        then:
        result.statusCode == HttpStatus.OK
        result.body == response
    }

    void "delete returns no content"() {
        given:
        UUID id = UUID.randomUUID()
        CustomUserDetails principal = new CustomUserDetails(new User(
                id: id,
                email: 'ana@example.com',
                role: UserRole.CANDIDATE))

        when:
        def result = controller.delete(principal)

        then:
        result.statusCode == HttpStatus.NO_CONTENT
        1 * candidateService.delete(id)
    }
}
