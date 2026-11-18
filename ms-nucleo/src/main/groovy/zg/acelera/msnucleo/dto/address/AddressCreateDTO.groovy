package zg.acelera.msnucleo.dto.address

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

class AddressCreateDTO {
    UUID userId

    @Size(max = 20, message = "CEP must have at most 20 characters")
    String cep

    @Size(max = 100, message = "Street must have at most 100 characters")
    String street

    @Size(max = 10, message = "Number must have at most 10 characters")
    String number

    @Size(max = 100, message = "Complement must have at most 100 characters")
    String complement

    @Size(max = 100, message = "Neighborhood must have at most 100 characters")
    String neighborhood

    @NotBlank(message = "City cannot be blank")
    @Size(max = 100, message = "City must have at most 100 characters")
    String city

    @NotBlank(message = "State cannot be blank")
    @Size(max = 100, message = "State must have at most 100 characters")
    String state

    @NotNull(message = "Country id cannot be null")
    UUID countryId
}
