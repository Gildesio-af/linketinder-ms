package zg.acelera.msnucleo.repositories.job

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import zg.acelera.msnucleo.domain.JobLike
import zg.acelera.msnucleo.domain.JobLikeId

interface JobLikeRepository {
    Optional<JobLike> findById(JobLikeId id)
    Page<JobLike> findByCandidateId(UUID candidateId, Pageable pageable)
    List<JobLike> findAllByCandidateId(UUID candidateId)
    JobLike save(JobLike jobLike)
    boolean existsById(JobLikeId id)
    void deleteById(JobLikeId id)
}
