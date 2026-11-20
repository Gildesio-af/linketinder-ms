package zg.acelera.msnucleo.repositories

import org.springframework.data.domain.Pageable
import spock.lang.Specification
import zg.acelera.msnucleo.domain.Address
import zg.acelera.msnucleo.repositories.address.AddressRepositoryImp
import zg.acelera.msnucleo.repositories.address.AddressRepositoryJpa

class AddressRepositoryImpTest extends Specification {
    AddressRepositoryJpa jpa
    AddressRepositoryImp repository

    def setup() {
        jpa = Mock()
        repository = new AddressRepositoryImp(jpa)
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

    void "findByUserId delegates to JPA repository"() {
        given:
        UUID userId = UUID.randomUUID()

        when:
        repository.findByUserId(userId)

        then:
        1 * jpa.findAllByUser_Id(userId)
    }

    void "save delegates to JPA repository"() {
        given:
        Address address = new Address(id: UUID.randomUUID())

        when:
        repository.save(address)

        then:
        1 * jpa.save(address)
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
