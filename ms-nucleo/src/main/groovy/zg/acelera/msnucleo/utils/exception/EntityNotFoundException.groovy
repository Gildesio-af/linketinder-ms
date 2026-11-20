package zg.acelera.msnucleo.utils.exception

import org.springframework.http.HttpStatus

class EntityNotFoundException extends ApiException {
    EntityNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND)
    }
}
