package zg.acelera.msnucleo.dto.user

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

class UserCreateDTO {
    @NotBlank(message = "Name cannot be blank")
    @Size(max = 100, message = "Name must have at most 100 characters")
    String name

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email must be valid")
    @Size(max = 100, message = "Email must have at most 100 characters")
    String email

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 6, max = 100, message = "Password must have between 6 and 100 characters")
    String password

    @NotBlank(message = "Description cannot be blank")
    String description

}
