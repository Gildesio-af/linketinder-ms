package zg.acelera.msnucleo.repositories

import org.springframework.data.domain.Pageable
import spock.lang.Specification
import zg.acelera.msnucleo.domain.Country
import zg.acelera.msnucleo.repositories.country.CountryRepositoryImp
import zg.acelera.msnucleo.repositories.country.CountryRepositoryJpa

class CountryRepositoryImpTest extends Specification {
    CountryRepositoryJpa jpa
    CountryRepositoryImp repository

    def setup() {
        jpa = Mock()
        repository = new CountryRepositoryImp(jpa)
    }

    void "findById delegates to JPA repository"() {
        given:
        UUID id = UUID.randomUUID()

        when:
        repository.findById(id)

        then:
        1 * jpa.findById(id)
    }

    void "findAll delegates to JPA repository"() {
        given:
        Pageable pageable = Mock()

        when:
        repository.findAll(pageable)

        then:
        1 * jpa.findAll(pageable)
    }

    void "findByNameIgnoreCase delegates to JPA repository"() {
        given:
        String name = 'Brazil'

        when:
        repository.findByNameIgnoreCase(name)

        then:
        1 * jpa.findByNameIgnoreCase(name)
    }

    void "findByNameContainingIgnoreCase delegates to JPA repository"() {
        given:
        String name = 'Bra'
        Pageable pageable = Mock()

        when:
        repository.findByNameContainingIgnoreCase(name, pageable)

        then:
        1 * jpa.findByNameContainingIgnoreCase(name, pageable)
    }

    void "findByCodeIgnoreCase delegates to JPA repository"() {
        given:
        String code = 'BR'

        when:
        repository.findByCodeIgnoreCase(code)

        then:
        1 * jpa.findByCodeIgnoreCase(code)
    }

    void "save delegates to JPA repository"() {
        given:
        Country country = new Country(id: UUID.randomUUID())

        when:
        repository.save(country)

        then:
        1 * jpa.save(country)
    }

    void "existsById delegates to JPA repository"() {
        given:
        UUID id = UUID.randomUUID()

        when:
        repository.existsById(id)

        then:
        1 * jpa.existsById(id)
    }

    void "delete delegates to JPA repository"() {
        given:
        UUID id = UUID.randomUUID()

        when:
        repository.delete(id)

        then:
        1 * jpa.deleteById(id)
    }
}
