package zg.acelera.utils.exception.handler

import org.springframework.http.HttpStatus
import org.springframework.web.context.request.WebRequest
import spock.lang.Specification
import spock.lang.Subject
import zg.acelera.dto.error.CustomError

class GlobalExceptionHandlerSpec extends Specification {

    @Subject
    GlobalExceptionHandler handler = new GlobalExceptionHandler()

    def "illegal argument exception returns bad request"() {
        given:
        WebRequest request = Mock()
        request.getDescription(false) >> "uri=/api/skills"

        when:
        def response = handler.handleIllegalArgumentException(
                new IllegalArgumentException("Skill already exists"),
                request)

        then:
        response.statusCode == HttpStatus.BAD_REQUEST
        response.body instanceof CustomError
        response.body.error == "Skill already exists"
        response.body.path == "uri=/api/skills"
        response.body.status == HttpStatus.BAD_REQUEST.value()
    }
}
