package zg.acelera.msnucleo.repositories

import org.springframework.data.domain.Pageable
import spock.lang.Specification
import zg.acelera.msnucleo.domain.Candidate
import zg.acelera.msnucleo.repositories.candidate.CandidateRepositoryImp
import zg.acelera.msnucleo.repositories.candidate.CandidateRepositoryJpa

class CandidateRepositoryImpTest extends Specification {
    CandidateRepositoryJpa jpa
    CandidateRepositoryImp repository

    def setup() {
        jpa = Mock()
        repository = new CandidateRepositoryImp(jpa)
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

    void "findByCpf delegates to JPA repository"() {
        given:
        String cpf = '12345678900'

        when:
        repository.findByCpf(cpf)

        then:
        1 * jpa.findByCpf(cpf)
    }

    void "save delegates to JPA repository"() {
        given:
        Candidate candidate = new Candidate(userId: UUID.randomUUID())

        when:
        repository.save(candidate)

        then:
        1 * jpa.save(candidate)
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
