package zg.acelera.msnucleo.repositories.job

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import zg.acelera.msnucleo.domain.Job

interface JobRepository {
    Optional<Job> findById(UUID id)
    Page<Job> findAll(Pageable pageable)
    Page<Job> findByPublisherId(UUID publisherId, Pageable pageable)
    Job save(Job job)
    boolean existsById(UUID id)
    void delete(UUID id)
}
