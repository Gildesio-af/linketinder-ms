package zg.acelera.msnucleo.controller

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import spock.lang.Specification
import zg.acelera.msnucleo.domain.User
import zg.acelera.msnucleo.domain.UserRole
import zg.acelera.msnucleo.dto.match.MatchResponseDTO
import zg.acelera.msnucleo.infrastructure.security.CustomUserDetails
import zg.acelera.msnucleo.service.MatchService

class MatchControllerTest extends Specification {
    MatchService matchService
    MatchController controller

    def setup() {
        matchService = Mock()
        controller = new MatchController(matchService)
    }

    void "candidate reads matches for authenticated candidate"() {
        given:
        UUID candidateId = UUID.randomUUID()
        PageRequest pageable = PageRequest.of(0, 10)
        CustomUserDetails principal = new CustomUserDetails(new User(
                id: candidateId,
                role: UserRole.CANDIDATE))
        Page<MatchResponseDTO> page = new PageImpl([], pageable, 0)
        matchService.findByCandidateId(candidateId, pageable) >> page

        when:
        def result = controller.findAll(principal, pageable)

        then:
        result.statusCode == HttpStatus.OK
        result.body == page
    }

    void "company reads matches for authenticated company"() {
        given:
        UUID companyId = UUID.randomUUID()
        PageRequest pageable = PageRequest.of(0, 10)
        CustomUserDetails principal = new CustomUserDetails(new User(
                id: companyId,
                role: UserRole.COMPANY))
        Page<MatchResponseDTO> page = new PageImpl([], pageable, 0)
        matchService.findByCompanyId(companyId, pageable) >> page

        when:
        def result = controller.findAll(principal, pageable)

        then:
        result.statusCode == HttpStatus.OK
        result.body == page
    }
}
