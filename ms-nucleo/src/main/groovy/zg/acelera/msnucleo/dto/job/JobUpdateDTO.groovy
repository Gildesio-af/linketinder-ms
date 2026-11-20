package zg.acelera.msnucleo.dto.job

import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

class JobUpdateDTO {
    @Size(max = 100, message = "Name must have at most 100 characters")
    @Pattern(regexp = ".*\\S.*", message = "Name cannot be blank")
    String name

    @Pattern(regexp = ".*\\S.*", message = "Description cannot be blank")
    String description

    UUID addressId
}
