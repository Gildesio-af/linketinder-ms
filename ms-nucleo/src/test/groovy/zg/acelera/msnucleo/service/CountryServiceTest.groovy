package zg.acelera.msnucleo.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import spock.lang.Specification
import zg.acelera.msnucleo.domain.Country
import zg.acelera.msnucleo.dto.country.CountryCreateDTO
import zg.acelera.msnucleo.dto.country.CountryResponseDTO
import zg.acelera.msnucleo.dto.country.CountryUpdateDTO
import zg.acelera.msnucleo.repositories.country.CountryRepository
import zg.acelera.msnucleo.utils.exception.EntityNotFoundException

class CountryServiceTest extends Specification {
    CountryRepository repository
    CountryService service

    def setup() {
        repository = Mock()
        service = new CountryService(repository)
    }

    void "findById returns country"() {
        given:
        UUID id = UUID.randomUUID()
        Country country = country(id, 'Brazil', 'BR')
        repository.findById(id) >> Optional.of(country)

        when:
        CountryResponseDTO result = service.findById(id)

        then:
        result.id == id
        result.name == 'Brazil'
        result.code == 'BR'
    }

    void "findById throws when country does not exist"() {
        given:
        UUID id = UUID.randomUUID()
        repository.findById(id) >> Optional.empty()

        when:
        service.findById(id)

        then:
        thrown(EntityNotFoundException)
    }

    void "findAll maps countries"() {
        given:
        PageRequest pageable = PageRequest.of(0, 10)
        Country country = country(UUID.randomUUID(), 'Brazil', 'BR')
        repository.findAll(pageable) >> new PageImpl([country], pageable, 1)

        when:
        Page<CountryResponseDTO> result = service.findAll(pageable)

        then:
        result.totalElements == 1
        result.content[0].name == 'Brazil'
    }

    void "findByName trims search term"() {
        given:
        PageRequest pageable = PageRequest.of(0, 10)
        when:
        Page<CountryResponseDTO> result = service.findByName(' Bra ', pageable)

        then:
        result.content[0].name == 'Brazil'
        1 * repository.findByNameContainingIgnoreCase('Bra', pageable) >>
                new PageImpl([country(UUID.randomUUID(), 'Brazil', 'BR')], pageable, 1)
    }

    void "findByName rejects blank term"() {
        when:
        service.findByName('  ', PageRequest.of(0, 10))

        then:
        thrown(IllegalArgumentException)
        0 * repository._
    }

    void "findByCode trims code"() {
        given:
        when:
        CountryResponseDTO result = service.findByCode(' br ')

        then:
        result.code == 'BR'
        1 * repository.findByCodeIgnoreCase('br') >>
                Optional.of(country(UUID.randomUUID(), 'Brazil', 'BR'))
    }

    void "save normalizes values and returns saved country"() {
        given:
        CountryCreateDTO dto = new CountryCreateDTO(name: ' Brazil ', code: 'br')
        repository.findByNameIgnoreCase('Brazil') >> Optional.empty()
        repository.findByCodeIgnoreCase('BR') >> Optional.empty()
        when:
        CountryResponseDTO result = service.save(dto)

        then:
        result.name == 'Brazil'
        result.code == 'BR'
        1 * repository.save({ Country value -> value.name == 'Brazil' && value.code == 'BR' }) >>
                { Country value -> value }
    }

    void "save rejects duplicate name"() {
        given:
        CountryCreateDTO dto = new CountryCreateDTO(name: 'Brazil', code: 'BR')
        repository.findByNameIgnoreCase('Brazil') >>
                Optional.of(country(UUID.randomUUID(), 'Brazil', 'BR'))

        when:
        service.save(dto)

        then:
        thrown(IllegalArgumentException)
        0 * repository.save(_)
    }

    void "update changes only provided values"() {
        given:
        UUID id = UUID.randomUUID()
        Country existing = country(id, 'Brazil', 'BR')
        repository.findById(id) >> Optional.of(existing)
        repository.findByNameIgnoreCase('Argentina') >> Optional.empty()
        repository.findByCodeIgnoreCase('AR') >> Optional.empty()
        when:
        CountryResponseDTO result = service.update(
                id, new CountryUpdateDTO(name: ' Argentina ', code: 'ar'))

        then:
        result.name == 'Argentina'
        result.code == 'AR'
        1 * repository.save(existing) >> existing
    }

    void "update allows existing values for same country"() {
        given:
        UUID id = UUID.randomUUID()
        Country existing = country(id, 'Brazil', 'BR')
        repository.findById(id) >> Optional.of(existing)
        repository.findByNameIgnoreCase('Brazil') >> Optional.of(existing)
        repository.findByCodeIgnoreCase('BR') >> Optional.of(existing)
        when:
        service.update(id, new CountryUpdateDTO(name: 'Brazil', code: 'BR'))

        then:
        1 * repository.save(existing) >> existing
    }

    void "delete removes existing country"() {
        given:
        UUID id = UUID.randomUUID()
        repository.existsById(id) >> true

        when:
        service.delete(id)

        then:
        1 * repository.delete(id)
    }

    void "delete throws when country does not exist"() {
        given:
        UUID id = UUID.randomUUID()
        repository.existsById(id) >> false

        when:
        service.delete(id)

        then:
        thrown(EntityNotFoundException)
        0 * repository.delete(_)
    }

    private static Country country(UUID id, String name, String code) {
        new Country(id: id, name: name, code: code)
    }
}
