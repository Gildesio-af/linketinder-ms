package zg.acelera.msnucleo.dto.country

import zg.acelera.msnucleo.domain.Country

class CountryResponseDTO {
    UUID id
    String name
    String code

    static CountryResponseDTO fromDomain(Country country) {
        new CountryResponseDTO(
                id: country.id,
                name: country.name,
                code: country.code)
    }
}
