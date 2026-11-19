package zg.acelera.msnucleo.domain

import spock.lang.Specification
import zg.acelera.msnucleo.dto.address.AddressResponseDTO
import zg.acelera.msnucleo.dto.company.CompanyResponseDTO

class CompanyTest extends Specification {
    void "company response maps domain and addresses"() {
        given:
        UUID id = UUID.randomUUID()
        User user = new User(
                id: id,
                name: 'Acme',
                email: 'contato@acme.com',
                description: 'Technology company')
        Company company = new Company(
                userId: id,
                user: user,
                cnpj: '12.345.678/0001-90')
        AddressResponseDTO address = new AddressResponseDTO(city: 'São Paulo')

        when:
        CompanyResponseDTO result = CompanyResponseDTO.fromDomain(company, [address])

        then:
        result.id == id
        result.name == 'Acme'
        result.email == 'contato@acme.com'
        result.description == 'Technology company'
        result.cnpj == '12.345.678/0001-90'
        result.addresses == [address]
    }

    void "company response uses empty address list by default"() {
        given:
        UUID id = UUID.randomUUID()
        Company company = new Company(
                userId: id,
                user: new User(
                        id: id,
                        name: 'Acme',
                        email: 'contato@acme.com',
                        description: 'Technology company'),
                cnpj: '12.345.678/0001-90')

        when:
        CompanyResponseDTO result = CompanyResponseDTO.fromDomain(company)

        then:
        result.addresses == []
    }
}
