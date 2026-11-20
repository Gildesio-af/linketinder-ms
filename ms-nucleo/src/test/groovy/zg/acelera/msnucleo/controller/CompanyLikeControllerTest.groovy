package zg.acelera.msnucleo.controller

import org.springframework.http.HttpStatus
import spock.lang.Specification
import zg.acelera.msnucleo.dto.company.CompanyLikeCreateDTO
import zg.acelera.msnucleo.dto.company.CompanyLikeResponseDTO
import zg.acelera.msnucleo.domain.User
import zg.acelera.msnucleo.domain.UserRole
import zg.acelera.msnucleo.infrastructure.security.CustomUserDetails
import zg.acelera.msnucleo.service.CompanyLikeService

class CompanyLikeControllerTest extends Specification {
    CompanyLikeService companyLikeService
    CompanyLikeController controller

    def setup() {
        companyLikeService = Mock()
        controller = new CompanyLikeController(companyLikeService)
    }

    void "save creates company like"() {
        given:
        UUID companyId = UUID.randomUUID()
        UUID candidateId = UUID.randomUUID()
        CustomUserDetails principal = new CustomUserDetails(new User(
                id: companyId,
                email: 'contato@acme.com',
                role: UserRole.COMPANY))
        CompanyLikeCreateDTO dto = new CompanyLikeCreateDTO(candidateId: candidateId)
        CompanyLikeResponseDTO response = new CompanyLikeResponseDTO(
                companyId: companyId,
                candidateId: candidateId)
        companyLikeService.save(companyId, dto) >> response

        when:
        def result = controller.save(principal, dto)

        then:
        result.statusCode == HttpStatus.CREATED
        result.body == response
    }

    void "delete returns no content"() {
        given:
        UUID candidateId = UUID.randomUUID()
        UUID companyId = UUID.randomUUID()
        CustomUserDetails principal = new CustomUserDetails(new User(
                id: companyId,
                email: 'contato@acme.com',
                role: UserRole.COMPANY))

        when:
        def result = controller.delete(principal, candidateId)

        then:
        result.statusCode == HttpStatus.NO_CONTENT
        1 * companyLikeService.delete(
                companyId,
                candidateId)
    }
}
