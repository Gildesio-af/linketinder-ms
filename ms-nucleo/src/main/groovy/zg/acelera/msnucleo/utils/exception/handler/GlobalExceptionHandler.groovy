package zg.acelera.msnucleo.utils.exception.handler

import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import zg.acelera.msnucleo.dto.error.CustomError
import zg.acelera.msnucleo.utils.exception.ApiException

@ControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(ApiException)
    ResponseEntity<Object> handleApiException(ApiException e, WebRequest request) {
        CustomError<String> error = new CustomError<>(
                e.message,
                request.getDescription(false),
                e.status)
        return new ResponseEntity<>(error, e.status)
    }

    @ExceptionHandler(IllegalArgumentException)
    ResponseEntity<Object> handleIllegalArgumentException(
            IllegalArgumentException e, WebRequest request) {
        CustomError<String> error = new CustomError<>(
                e.message,
                request.getDescription(false),
                HttpStatus.CONFLICT)
        return new ResponseEntity<>(error, HttpStatus.CONFLICT)
    }

    @ExceptionHandler(MethodArgumentNotValidException)
    ResponseEntity<Object> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e, WebRequest request) {
        List<Map<String, String>> messages = e.bindingResult.fieldErrors.collect { fieldError ->
            [
                    field  : fieldError.field.toString(),
                    message: fieldError.defaultMessage?.toString()
            ]
        }
        CustomError<List<Map<String, String>>> error = new CustomError<>(
                messages,
                request.getDescription(false),
                HttpStatus.BAD_REQUEST)
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(HttpMessageNotReadableException)
    ResponseEntity<Object> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException e, WebRequest request) {
        CustomError<String> error = new CustomError<>(
                e.message,
                request.getDescription(false),
                HttpStatus.BAD_REQUEST)
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(DataIntegrityViolationException)
    ResponseEntity<Object> handleDataIntegrityViolationException(
            DataIntegrityViolationException e, WebRequest request) {
        CustomError<String> error = new CustomError<>(
                "The operation conflicts with existing data",
                request.getDescription(false),
                HttpStatus.CONFLICT)
        return new ResponseEntity<>(error, HttpStatus.CONFLICT)
    }
}
