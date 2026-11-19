package zg.acelera.msnucleo.controller

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import spock.lang.Specification
import zg.acelera.msnucleo.dto.company.CompanyCreateDTO
import zg.acelera.msnucleo.dto.company.CompanyDataCreateDTO
import zg.acelera.msnucleo.dto.company.CompanyResponseDTO
import zg.acelera.msnucleo.dto.company.CompanyUpdateDTO
import zg.acelera.msnucleo.dto.user.UserCreateDTO
import zg.acelera.msnucleo.service.CompanyService

class CompanyControllerTest extends Specification {
    CompanyService companyService
    CompanyController controller

    def setup() {
        companyService = Mock()
        controller = new CompanyController(companyService)
    }

    def cleanup() {
        RequestContextHolder.resetRequestAttributes()
    }

    void "findById returns company"() {
        given:
        UUID id = UUID.randomUUID()
        CompanyResponseDTO response = new CompanyResponseDTO(id: id)
        companyService.findById(id) >> response

        when:
        def result = controller.findById(id)

        then:
        result.statusCode == HttpStatus.OK
        result.body == response
    }

    void "findAll returns company page"() {
        given:
        PageRequest pageable = PageRequest.of(0, 10)
        CompanyResponseDTO company = new CompanyResponseDTO(id: UUID.randomUUID())
        Page<CompanyResponseDTO> page = new PageImpl([company], pageable, 1)
        companyService.findAll(pageable) >> page

        when:
        def result = controller.findAll(pageable)

        then:
        result.statusCode == HttpStatus.OK
        result.body == page
    }

    void "save returns created response"() {
        given:
        UUID id = UUID.randomUUID()
        CompanyCreateDTO dto = new CompanyCreateDTO(
                user: new UserCreateDTO(
                        name: 'Acme',
                        email: 'contato@acme.com',
                        password: 'secret1',
                        description: 'Technology company'),
                company: new CompanyDataCreateDTO(cnpj: '12.345.678/0001-90'))
        CompanyResponseDTO saved = new CompanyResponseDTO(id: id)
        companyService.save(dto) >> saved
        MockHttpServletRequest request = new MockHttpServletRequest(
                'POST', '/companies')
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request))

        when:
        def result = controller.save(dto)

        then:
        result.statusCode == HttpStatus.CREATED
        result.body == saved
        result.headers.location != null
    }

    void "update returns updated company"() {
        given:
        UUID id = UUID.randomUUID()
        CompanyUpdateDTO dto = new CompanyUpdateDTO(name: 'Acme Brasil')
        CompanyResponseDTO response = new CompanyResponseDTO(
                id: id, name: 'Acme Brasil')
        companyService.update(id, dto) >> response

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
        1 * companyService.delete(id)
    }
}
