package zg.acelera.msnucleo.repositories

import org.springframework.data.domain.Pageable
import spock.lang.Specification
import zg.acelera.msnucleo.domain.Company
import zg.acelera.msnucleo.repositories.company.CompanyRepositoryImp
import zg.acelera.msnucleo.repositories.company.CompanyRepositoryJpa

class CompanyRepositoryImpTest extends Specification {
    CompanyRepositoryJpa jpa
    CompanyRepositoryImp repository

    def setup() {
        jpa = Mock()
        repository = new CompanyRepositoryImp(jpa)
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

    void "findByCnpj delegates to JPA repository"() {
        given:
        String cnpj = '12.345.678/0001-90'

        when:
        repository.findByCnpj(cnpj)

        then:
        1 * jpa.findByCnpj(cnpj)
    }

    void "save delegates to JPA repository"() {
        given:
        Company company = new Company(userId: UUID.randomUUID())

        when:
        repository.save(company)

        then:
        1 * jpa.save(company)
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
