package zg.acelera.utils.exception.handler

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import zg.acelera.dto.error.CustomError
import zg.acelera.utils.exception.ApiException

@ControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(ApiException.class)
    ResponseEntity<Object> handleApiException(ApiException e, WebRequest request){
        CustomError<String> error = new CustomError<>(e.getMessage(),
                request.getDescription(false), e.getStatus());

        return new ResponseEntity<>(error, e.getStatus());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<Object> handleIllegalArgumentException(
            IllegalArgumentException e, WebRequest request) {
        CustomError<String> error = new CustomError<>(
                e.getMessage(),
                request.getDescription(false),
                HttpStatus.CONFLICT)

        return new ResponseEntity<>(error, HttpStatus.CONFLICT)
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<Object> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e, WebRequest request) {
        List<Map<String, String>> messages = e.getBindingResult()
                .getFieldErrors()
                .collect { fieldError ->
                    [
                            field   : fieldError.field.toString(),
                            message : fieldError.defaultMessage?.toString()
                    ]
                }

        CustomError<List<Map<String, String>>> error = new CustomError<>(
                messages,
                request.getDescription(false),
                HttpStatus.BAD_REQUEST)

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<Object> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException e, WebRequest request) {
        CustomError<String> error = new CustomError<>(
                e.getMessage(),
                request.getDescription(false),
                HttpStatus.BAD_REQUEST)

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST)
    }
}
