package zg.acelera.msnucleo.dto.company

import jakarta.validation.Valid
import jakarta.validation.constraints.NotNull
import zg.acelera.msnucleo.dto.user.UserCreateDTO

class CompanyCreateDTO {
    @Valid
    @NotNull(message = "User data cannot be null")
    UserCreateDTO user

    @Valid
    @NotNull(message = "Company data cannot be null")
    CompanyDataCreateDTO company
}
