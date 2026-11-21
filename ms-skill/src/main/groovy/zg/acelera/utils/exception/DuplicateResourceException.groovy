package zg.acelera.utils.exception

import org.springframework.http.HttpStatus

class DuplicateResourceException extends ApiException {
    DuplicateResourceException(String message) {
        super(message, HttpStatus.CONFLICT)
    }
}
