package zg.acelera.msnucleo.dto.country

import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

class CountryUpdateDTO {
    @Size(max = 75, message = "Country name must have at most 75 characters")
    @Pattern(regexp = ".*\\S.*", message = "Country name cannot be blank")
    private String name

    @Size(min = 2, max = 2, message = "Country code must have exactly 2 characters")
    @Pattern(regexp = "[A-Za-z]{2}", message = "Country code must contain exactly 2 letters")
    private String code

    String getName() { name }
    void setName(String name) { this.name = name }
    String name() { name }

    String getCode() { code }
    void setCode(String code) { this.code = code }
    String code() { code }
}
