package zg.acelera.msnucleo.dto.candidate

import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Past
import jakarta.validation.constraints.Size

import java.time.LocalDate

class CandidateDataCreateDTO {
    @NotBlank(message = "CPF cannot be blank")
    @Size(max = 14, message = "CPF must have at most 14 characters")
    String cpf

    @NotBlank(message = "Last name cannot be blank")
    @Size(max = 100, message = "Last name must have at most 100 characters")
    String lastName

    @NotNull(message = "Birth date cannot be null")
    @Past(message = "Birth date must be in the past")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    LocalDate birthDate
}
