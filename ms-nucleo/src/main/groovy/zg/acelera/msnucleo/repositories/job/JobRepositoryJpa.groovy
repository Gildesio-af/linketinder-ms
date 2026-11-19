package zg.acelera.msnucleo.repositories.job

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import zg.acelera.msnucleo.domain.Job

interface JobRepositoryJpa extends JpaRepository<Job, UUID> {
    Page<Job> findAllByPublisher_UserId(UUID publisherId, Pageable pageable)
}
