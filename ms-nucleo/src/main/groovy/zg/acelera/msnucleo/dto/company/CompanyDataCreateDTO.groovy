package zg.acelera.msnucleo.dto.company

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

class CompanyDataCreateDTO {
    @NotBlank(message = "CNPJ cannot be blank")
    @Size(max = 18, message = "CNPJ must have at most 18 characters")
    @Pattern(regexp = "[0-9./-]+", message = "CNPJ contains invalid characters")
    String cnpj
}
