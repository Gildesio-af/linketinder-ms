package zg.acelera.msnucleo.dto.company

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

class CompanyUpdateDTO {
    @Size(max = 100, message = "Name must have at most 100 characters")
    @Pattern(regexp = ".*\\S.*", message = "Name cannot be blank")
    String name

    @Email(message = "Email must be valid")
    @Size(max = 100, message = "Email must have at most 100 characters")
    String email

    @Pattern(regexp = ".*\\S.*", message = "Description cannot be blank")
    String description

    @Size(max = 18, message = "CNPJ must have at most 18 characters")
    @Pattern(regexp = ".*\\S.*", message = "CNPJ cannot be blank")
    String cnpj
}
