package zg.acelera.utils.exception

import org.springframework.http.HttpStatus

class ApiException extends RuntimeException {
    private final HttpStatus status

    ApiException(String message, HttpStatus status) {
        super(message)
        this.status = status
    }

    HttpStatus getStatus() {
        return status
    }
}
