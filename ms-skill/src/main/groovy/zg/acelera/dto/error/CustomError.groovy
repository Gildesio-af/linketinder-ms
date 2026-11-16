package zg.acelera.dto.error

import com.fasterxml.jackson.annotation.JsonFormat
import org.springframework.http.HttpStatus

import java.time.LocalDateTime

class CustomError<T> {
    final T error
    final String path
    final int status
    @JsonFormat(pattern = "yyyy-MM-dd' 'HH:mm:ss")
    final LocalDateTime timestamp

    CustomError(T error, String path, HttpStatus status) {
        this.error = error
        this.path = path
        this.status = status.value()
        this.timestamp = LocalDateTime.now()
    }

}
