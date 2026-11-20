package zg.acelera.msnucleo.utils.exception

import org.springframework.http.HttpStatus

class InvalidTokenException extends ApiException{

    InvalidTokenException(String message) {
        super(message, HttpStatus.UNAUTHORIZED)
    }
}
