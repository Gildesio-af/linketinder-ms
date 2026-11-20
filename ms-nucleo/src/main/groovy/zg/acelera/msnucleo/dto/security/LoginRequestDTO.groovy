package zg.acelera.msnucleo.dto.security


import jakarta.validation.constraints.NotBlank
import org.hibernate.validator.constraints.Length

class LoginRequestDTO {
    @NotBlank(message = "Email cannot be blank")
    @Length(max = 100, message = "Email must have at most 100 characters")
    String email
    @NotBlank(message = "Password cannot be blank")
    @Length(max = 100, message = "Password must have at most 100 characters")
    String password
}
