package zg.acelera.msnucleo.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import spock.lang.Specification
import zg.acelera.msnucleo.domain.Address
import zg.acelera.msnucleo.domain.Company
import zg.acelera.msnucleo.domain.Country
import zg.acelera.msnucleo.domain.User
import zg.acelera.msnucleo.domain.UserRole
import zg.acelera.msnucleo.dto.company.CompanyCreateDTO
import zg.acelera.msnucleo.dto.company.CompanyDataCreateDTO
import zg.acelera.msnucleo.dto.company.CompanyResponseDTO
import zg.acelera.msnucleo.dto.company.CompanyUpdateDTO
import zg.acelera.msnucleo.dto.user.UserCreateDTO
import zg.acelera.msnucleo.repositories.address.AddressRepository
import zg.acelera.msnucleo.repositories.company.CompanyRepository
import zg.acelera.msnucleo.repositories.user.UserRepository
import zg.acelera.msnucleo.utils.exception.EntityNotFoundException

class CompanyServiceTest extends Specification {
    CompanyRepository companyRepository
    UserRepository userRepository
    AddressRepository addressRepository
    UserService userService
    CompanyService service

    def setup() {
        companyRepository = Mock()
        userRepository = Mock()
        addressRepository = Mock()
        userService = Mock()
        service = new CompanyService(companyRepository, userRepository,
                addressRepository, userService)
    }

    void "findById returns company with addresses"() {
        given:
        UUID id = UUID.randomUUID()
        Company company = buildCompany(id)
        companyRepository.findById(id) >> Optional.of(company)
        addressRepository.findByUserId(id) >> [address(company.user)]

        when:
        CompanyResponseDTO result = service.findById(id)

        then:
        result.id == id
        result.name == 'Acme'
        result.addresses.size() == 1
    }

    void "findById throws when company does not exist"() {
        given:
        UUID id = UUID.randomUUID()
        companyRepository.findById(id) >> Optional.empty()

        when:
        service.findById(id)

        then:
        thrown(EntityNotFoundException)
    }

    void "findAll maps companies"() {
        given:
        PageRequest pageable = PageRequest.of(0, 10)
        Company company = buildCompany(UUID.randomUUID())
        companyRepository.findAll(pageable) >> new PageImpl([company], pageable, 1)
        addressRepository.findByUserId(company.userId) >> []

        when:
        Page<CompanyResponseDTO> result = service.findAll(pageable)

        then:
        result.totalElements == 1
        result.content[0].cnpj == '12.345.678/0001-90'
    }

    void "save creates user and company"() {
        given:
        UUID id = UUID.randomUUID()
        User user = new User(id: id, name: 'Acme', email: 'contato@acme.com',
                password: '$2a$10$hashed-password', role: UserRole.COMPANY,
                description: 'Technology company')
        CompanyCreateDTO dto = createDto()
        1 * userService.create(dto.user, UserRole.COMPANY) >> user
        companyRepository.findByCnpj('12.345.678/0001-90') >> Optional.empty()
        addressRepository.findByUserId(id) >> []

        when:
        CompanyResponseDTO result = service.save(dto)

        then:
        result.name == 'Acme'
        result.cnpj == '12.345.678/0001-90'
        1 * companyRepository.save({
            Company value -> value.user == user &&
                    value.cnpj == '12.345.678/0001-90'
        }) >> { Company value ->
            value.userId = id
            value
        }
    }

    void "save rejects duplicate cnpj"() {
        given:
        User user = new User(id: UUID.randomUUID(), name: 'Acme')
        CompanyCreateDTO dto = createDto()
        1 * userService.create(dto.user, UserRole.COMPANY) >> user
        companyRepository.findByCnpj('12.345.678/0001-90') >>
                Optional.of(buildCompany(UUID.randomUUID()))

        when:
        service.save(dto)

        then:
        thrown(IllegalArgumentException)
        0 * companyRepository.save(_)
    }

    void "update changes provided company and user fields"() {
        given:
        UUID id = UUID.randomUUID()
        Company company = buildCompany(id)
        CompanyUpdateDTO dto = new CompanyUpdateDTO(
                name: ' Acme Brasil ',
                email: ' CONTATO.NOVO@ACME.COM ',
                description: ' International company ',
                cnpj: ' 98.765.432/0001-10 ')
        companyRepository.findById(id) >> Optional.of(company)
        userRepository.findByEmailIgnoreCase('contato.novo@acme.com') >> Optional.empty()
        companyRepository.findByCnpj('98.765.432/0001-10') >> Optional.empty()
        addressRepository.findByUserId(id) >> []

        when:
        CompanyResponseDTO result = service.update(id, dto)

        then:
        result.name == 'Acme Brasil'
        result.email == 'contato.novo@acme.com'
        result.description == 'International company'
        result.cnpj == '98.765.432/0001-10'
        1 * userRepository.save(company.user) >> company.user
        1 * companyRepository.save(company) >> company
    }

    void "update rejects duplicate email"() {
        given:
        UUID id = UUID.randomUUID()
        Company company = buildCompany(id)
        User existing = new User(id: UUID.randomUUID(), email: 'used@acme.com')
        companyRepository.findById(id) >> Optional.of(company)
        userRepository.findByEmailIgnoreCase('used@acme.com') >> Optional.of(existing)

        when:
        service.update(id, new CompanyUpdateDTO(email: ' used@acme.com '))

        then:
        thrown(IllegalArgumentException)
        0 * userRepository.save(_)
        0 * companyRepository.save(_)
    }

    void "update allows email from same company"() {
        given:
        UUID id = UUID.randomUUID()
        Company company = buildCompany(id)
        companyRepository.findById(id) >> Optional.of(company)
        userRepository.findByEmailIgnoreCase('contato@acme.com') >>
                Optional.of(company.user)
        addressRepository.findByUserId(id) >> []

        when:
        CompanyResponseDTO result = service.update(
                id, new CompanyUpdateDTO(email: ' contato@acme.com '))

        then:
        result.email == 'contato@acme.com'
        1 * userRepository.save(company.user) >> company.user
        1 * companyRepository.save(company) >> company
    }

    void "update rejects duplicate cnpj"() {
        given:
        UUID id = UUID.randomUUID()
        Company company = buildCompany(id)
        Company existing = buildCompany(UUID.randomUUID())
        companyRepository.findById(id) >> Optional.of(company)
        companyRepository.findByCnpj('98.765.432/0001-10') >> Optional.of(existing)

        when:
        service.update(id, new CompanyUpdateDTO(cnpj: '98.765.432/0001-10'))

        then:
        thrown(IllegalArgumentException)
        0 * companyRepository.save(_)
    }

    void "update keeps unprovided fields"() {
        given:
        UUID id = UUID.randomUUID()
        Company company = buildCompany(id)
        companyRepository.findById(id) >> Optional.of(company)
        addressRepository.findByUserId(id) >> []

        when:
        CompanyResponseDTO result = service.update(
                id, new CompanyUpdateDTO(description: ' Updated company '))

        then:
        result.description == 'Updated company'
        result.name == 'Acme'
        result.cnpj == '12.345.678/0001-90'
        1 * userRepository.save(company.user) >> company.user
        1 * companyRepository.save(company) >> company
    }

    void "delete removes existing company and user"() {
        given:
        UUID id = UUID.randomUUID()
        companyRepository.existsById(id) >> true
        userRepository.existsById(id) >> true

        when:
        service.delete(id)

        then:
        1 * companyRepository.delete(id)
        1 * userRepository.delete(id)
    }

    void "delete throws when company does not exist"() {
        given:
        UUID id = UUID.randomUUID()
        companyRepository.existsById(id) >> false

        when:
        service.delete(id)

        then:
        thrown(EntityNotFoundException)
        0 * companyRepository.delete(_)
        0 * userRepository.delete(_)
    }

    void "delete throws when user does not exist"() {
        given:
        UUID id = UUID.randomUUID()
        companyRepository.existsById(id) >> true
        userRepository.existsById(id) >> false

        when:
        service.delete(id)

        then:
        thrown(EntityNotFoundException)
        0 * companyRepository.delete(_)
        0 * userRepository.delete(_)
    }

    private static CompanyCreateDTO createDto() {
        new CompanyCreateDTO(
                user: new UserCreateDTO(
                        name: 'Acme',
                        email: 'contato@acme.com',
                        password: 'secret1',
                        description: 'Technology company'),
                company: new CompanyDataCreateDTO(
                        cnpj: ' 12.345.678/0001-90 '))
    }

    private static Company buildCompany(UUID id) {
        User user = new User(
                id: id,
                name: 'Acme',
                email: 'contato@acme.com',
                password: '$2a$10$hashed-password',
                role: UserRole.COMPANY,
                description: 'Technology company')
        new Company(
                userId: id,
                user: user,
                cnpj: '12.345.678/0001-90')
    }

    private static Address address(User user) {
        new Address(
                user: user,
                cep: '01000-000',
                city: 'São Paulo',
                state: 'SP',
                country: new Country(id: UUID.randomUUID(), name: 'Brazil', code: 'BR'))
    }
}
