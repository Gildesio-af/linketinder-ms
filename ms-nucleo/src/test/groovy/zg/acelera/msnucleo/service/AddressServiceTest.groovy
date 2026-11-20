package zg.acelera.msnucleo.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import spock.lang.Specification
import zg.acelera.msnucleo.domain.Address
import zg.acelera.msnucleo.domain.Country
import zg.acelera.msnucleo.domain.User
import zg.acelera.msnucleo.dto.address.AddressCreateDTO
import zg.acelera.msnucleo.dto.address.AddressResponseDTO
import zg.acelera.msnucleo.dto.address.AddressUpdateDTO
import zg.acelera.msnucleo.repositories.address.AddressRepository
import zg.acelera.msnucleo.repositories.country.CountryRepository
import zg.acelera.msnucleo.repositories.user.UserRepository
import zg.acelera.msnucleo.utils.exception.EntityNotFoundException

class AddressServiceTest extends Specification {
    AddressRepository addressRepository
    CountryRepository countryRepository
    UserRepository userRepository
    AddressService service

    def setup() {
        addressRepository = Mock()
        countryRepository = Mock()
        userRepository = Mock()
        service = new AddressService(addressRepository, countryRepository, userRepository)
    }

    void "findById returns address"() {
        given:
        UUID id = UUID.randomUUID()
        Address address = address(id)
        addressRepository.findById(id) >> Optional.of(address)

        when:
        AddressResponseDTO result = service.findById(id)

        then:
        result.id == id
        result.city == 'São Paulo'
        result.countryId == address.country.id
    }

    void "findById throws when address does not exist"() {
        given:
        UUID id = UUID.randomUUID()
        addressRepository.findById(id) >> Optional.empty()

        when:
        service.findById(id)

        then:
        thrown(EntityNotFoundException)
    }

    void "findAll and findByUserId map addresses"() {
        given:
        PageRequest pageable = PageRequest.of(0, 10)
        UUID userId = UUID.randomUUID()
        Address address = address(UUID.randomUUID())
        address.user = new User(id: userId)
        addressRepository.findAll(pageable) >> new PageImpl([address], pageable, 1)
        addressRepository.findByUserId(userId) >> [address]

        when:
        Page<AddressResponseDTO> page = service.findAll(pageable)
        List<AddressResponseDTO> byUser = service.findByUserId(userId)

        then:
        page.totalElements == 1
        byUser[0].userId == userId
    }

    void "save resolves relations and trims values"() {
        given:
        UUID countryId = UUID.randomUUID()
        UUID userId = UUID.randomUUID()
        Country country = new Country(id: countryId, name: 'Brazil', code: 'BR')
        User user = new User(id: userId)
        countryRepository.findById(countryId) >> Optional.of(country)
        userRepository.findById(userId) >> Optional.of(user)
        AddressCreateDTO dto = new AddressCreateDTO(
                userId: userId, countryId: countryId, cep: ' 01000-000 ',
                street: ' Paulista ', number: ' 100 ', city: ' São Paulo ', state: ' SP ')

        when:
        AddressResponseDTO result = service.save(dto)

        then:
        result.cep == '01000-000'
        result.street == 'Paulista'
        result.number == '100'
        result.city == 'São Paulo'
        result.userId == userId
        1 * addressRepository.save({
            Address value -> value.country == country && value.user == user && value.state == 'SP'
        }) >> { Address value -> value }
    }

    void "save allows address without user"() {
        given:
        UUID countryId = UUID.randomUUID()
        Country country = new Country(id: countryId, name: 'Brazil', code: 'BR')
        countryRepository.findById(countryId) >> Optional.of(country)
        addressRepository.save(_) >> { Address value -> value }

        when:
        AddressResponseDTO result = service.save(
                new AddressCreateDTO(countryId: countryId, city: 'São Paulo', state: 'SP'))

        then:
        result.userId == null
        0 * userRepository._
    }

    void "save throws when country does not exist"() {
        given:
        UUID countryId = UUID.randomUUID()
        countryRepository.findById(countryId) >> Optional.empty()

        when:
        service.save(new AddressCreateDTO(countryId: countryId, city: 'São Paulo', state: 'SP'))

        then:
        thrown(EntityNotFoundException)
        0 * addressRepository._
    }

    void "update changes provided fields and relations"() {
        given:
        UUID id = UUID.randomUUID()
        UUID countryId = UUID.randomUUID()
        UUID userId = UUID.randomUUID()
        Address existing = address(id)
        Country country = new Country(id: countryId, name: 'Argentina', code: 'AR')
        User user = new User(id: userId)
        addressRepository.findById(id) >> Optional.of(existing)
        countryRepository.findById(countryId) >> Optional.of(country)
        userRepository.findById(userId) >> Optional.of(user)
        when:
        AddressResponseDTO result = service.update(id, new AddressUpdateDTO(
                userId: userId, countryId: countryId, city: '  Buenos Aires ', state: ' BA '))

        then:
        result.city == 'Buenos Aires'
        result.state == 'BA'
        result.countryId == countryId
        result.userId == userId
        1 * addressRepository.save(existing) >> existing
    }

    void "update keeps unprovided fields"() {
        given:
        UUID id = UUID.randomUUID()
        Address existing = address(id)
        addressRepository.findById(id) >> Optional.of(existing)
        addressRepository.save(existing) >> existing

        when:
        service.update(id, new AddressUpdateDTO(city: ' Campinas '))

        then:
        existing.city == 'Campinas'
        existing.state == 'SP'
        existing.street == 'Paulista'
        0 * countryRepository._
        0 * userRepository._
    }

    void "delete removes existing address"() {
        given:
        UUID id = UUID.randomUUID()
        addressRepository.existsById(id) >> true

        when:
        service.delete(id)

        then:
        1 * addressRepository.delete(id)
    }

    void "delete throws when address does not exist"() {
        given:
        UUID id = UUID.randomUUID()
        addressRepository.existsById(id) >> false

        when:
        service.delete(id)

        then:
        thrown(EntityNotFoundException)
        0 * addressRepository.delete(_)
    }

    private static Address address(UUID id) {
        new Address(
                id: id,
                cep: '01000-000',
                street: 'Paulista',
                number: '100',
                city: 'São Paulo',
                state: 'SP',
                country: new Country(id: UUID.randomUUID(), name: 'Brazil', code: 'BR'))
    }
}
