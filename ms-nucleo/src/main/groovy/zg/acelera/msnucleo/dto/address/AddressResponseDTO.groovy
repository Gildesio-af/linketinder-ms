package zg.acelera.msnucleo.dto.address

import zg.acelera.msnucleo.domain.Address

class AddressResponseDTO {
    UUID id
    UUID userId
    String cep
    String street
    String number
    String complement
    String neighborhood
    String city
    String state
    UUID countryId
    String countryName
    String countryCode

    static AddressResponseDTO fromDomain(Address address) {
        new AddressResponseDTO(
                id: address.id,
                userId: address.user?.id,
                cep: address.cep,
                street: address.street,
                number: address.number,
                complement: address.complement,
                neighborhood: address.neighborhood,
                city: address.city,
                state: address.state,
                countryId: address.country.id,
                countryName: address.country.name,
                countryCode: address.country.code)
    }
}
