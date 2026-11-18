package zg.acelera.msnucleo.dto.candidate

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Past
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

import java.time.LocalDate

class CandidateUpdateDTO {
    @Size(max = 100, message = "Name must have at most 100 characters")
    @Pattern(regexp = ".*\\S.*", message = "Name cannot be blank")
    String name

    @Email(message = "Email must be valid")
    @Size(max = 100, message = "Email must have at most 100 characters")
    String email

    @Pattern(regexp = ".*\\S.*", message = "Description cannot be blank")
    String description

    @Size(max = 14, message = "CPF must have at most 14 characters")
    @Pattern(regexp = ".*\\S.*", message = "CPF cannot be blank")
    String cpf

    @Size(max = 100, message = "Last name must have at most 100 characters")
    @Pattern(regexp = ".*\\S.*", message = "Last name cannot be blank")
    String lastName

    @Past(message = "Birth date must be in the past")
    LocalDate birthDate
}
