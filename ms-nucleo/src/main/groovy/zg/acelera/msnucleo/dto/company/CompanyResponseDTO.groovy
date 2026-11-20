package zg.acelera.msnucleo.dto.company

import zg.acelera.msnucleo.domain.Company
import zg.acelera.msnucleo.dto.address.AddressResponseDTO

class CompanyResponseDTO {
    UUID id
    String name
    String email
    String description
    String cnpj
    List<AddressResponseDTO> addresses = []

    static CompanyResponseDTO fromDomain(Company company,
                                         List<AddressResponseDTO> addresses = []) {
        new CompanyResponseDTO(
                id: company.userId,
                name: company.user.name,
                email: company.user.email,
                description: company.user.description,
                cnpj: company.cnpj,
                addresses: addresses)
    }
}
