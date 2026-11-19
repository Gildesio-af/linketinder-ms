package zg.acelera.msnucleo.repositories.job

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import zg.acelera.msnucleo.domain.Job

@Repository
class JobRepositoryImp implements JobRepository {
    private final JobRepositoryJpa jobRepositoryJpa

    JobRepositoryImp(JobRepositoryJpa jobRepositoryJpa) {
        this.jobRepositoryJpa = jobRepositoryJpa
    }

    @Override Optional<Job> findById(UUID id) { jobRepositoryJpa.findById(id) }
    @Override Page<Job> findAll(Pageable pageable) { jobRepositoryJpa.findAll(pageable) }
    @Override Page<Job> findByPublisherId(UUID publisherId, Pageable pageable) {
        jobRepositoryJpa.findAllByPublisher_UserId(publisherId, pageable)
    }
    @Override Job save(Job job) { jobRepositoryJpa.save(job) }
    @Override boolean existsById(UUID id) { jobRepositoryJpa.existsById(id) }
    @Override void delete(UUID id) { jobRepositoryJpa.deleteById(id) }
}
