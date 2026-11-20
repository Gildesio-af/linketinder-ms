package zg.acelera.msnucleo.repositories.job

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import zg.acelera.msnucleo.domain.JobLike
import zg.acelera.msnucleo.domain.JobLikeId

interface JobLikeRepositoryJpa extends JpaRepository<JobLike, JobLikeId> {
    Page<JobLike> findByIdCandidateId(UUID candidateId, Pageable pageable)
}
