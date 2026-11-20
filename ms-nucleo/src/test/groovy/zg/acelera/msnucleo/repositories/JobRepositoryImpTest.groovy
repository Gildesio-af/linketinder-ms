package zg.acelera.msnucleo.repositories

import org.springframework.data.domain.Pageable
import spock.lang.Specification
import zg.acelera.msnucleo.domain.Job
import zg.acelera.msnucleo.repositories.job.JobRepositoryImp
import zg.acelera.msnucleo.repositories.job.JobRepositoryJpa

class JobRepositoryImpTest extends Specification {
    JobRepositoryJpa jpa
    JobRepositoryImp repository

    def setup() {
        jpa = Mock()
        repository = new JobRepositoryImp(jpa)
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

    void "findByPublisherId delegates to JPA repository"() {
        given:
        UUID publisherId = UUID.randomUUID()
        Pageable pageable = Mock()

        when:
        repository.findByPublisherId(publisherId, pageable)

        then:
        1 * jpa.findAllByPublisher_UserId(publisherId, pageable)
    }

    void "save delegates to JPA repository"() {
        given:
        Job job = new Job(id: UUID.randomUUID())

        when:
        repository.save(job)

        then:
        1 * jpa.save(job)
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
