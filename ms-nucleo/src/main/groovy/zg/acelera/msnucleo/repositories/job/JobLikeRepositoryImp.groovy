package zg.acelera.msnucleo.repositories.job

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import zg.acelera.msnucleo.domain.JobLike
import zg.acelera.msnucleo.domain.JobLikeId

@Repository
class JobLikeRepositoryImp implements JobLikeRepository {
    private final JobLikeRepositoryJpa jobLikeRepositoryJpa

    JobLikeRepositoryImp(JobLikeRepositoryJpa jobLikeRepositoryJpa) {
        this.jobLikeRepositoryJpa = jobLikeRepositoryJpa
    }

    @Override Optional<JobLike> findById(JobLikeId id) { jobLikeRepositoryJpa.findById(id) }
    @Override Page<JobLike> findByCandidateId(UUID candidateId, Pageable pageable) {
        jobLikeRepositoryJpa.findByIdCandidateId(candidateId, pageable)
    }
    @Override JobLike save(JobLike jobLike) { jobLikeRepositoryJpa.save(jobLike) }
    @Override boolean existsById(JobLikeId id) { jobLikeRepositoryJpa.existsById(id) }
    @Override void deleteById(JobLikeId id) { jobLikeRepositoryJpa.deleteById(id) }
}
