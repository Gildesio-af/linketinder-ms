package zg.acelera.msnucleo.dto.job

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

class JobCreateDTO {
    @NotBlank(message = "Name cannot be blank")
    @Size(max = 100, message = "Name must have at most 100 characters")
    String name

    @NotBlank(message = "Description cannot be blank")
    String description

    @NotNull(message = "Address id cannot be null")
    UUID addressId

    UUID publisherId
}
