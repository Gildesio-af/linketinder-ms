package zg.acelera.msnucleo.dto.country

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

class CountryUpdateDTO {
    @NotBlank(message = "Country name cannot be blank")
    @Size(max = 75, message = "Country name must have at most 75 characters")
    private String name

    @NotBlank(message = "Country code cannot be blank")
    @Size(min = 2, max = 2, message = "Country code must have exactly 2 characters")
    private String code

    String getName() { name }
    void setName(String name) { this.name = name }
    String name() { name }

    String getCode() { code }
    void setCode(String code) { this.code = code }
    String code() { code }
}
