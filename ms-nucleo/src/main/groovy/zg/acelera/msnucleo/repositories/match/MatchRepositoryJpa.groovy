package zg.acelera.msnucleo.repositories.match

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.transaction.annotation.Transactional
import zg.acelera.msnucleo.domain.Match

interface MatchRepositoryJpa extends JpaRepository<Match, UUID> {
    Page<Match> findAllByCandidate_UserId(UUID candidateId, Pageable pageable)
    Page<Match> findAllByCompany_UserId(UUID companyId, Pageable pageable)
    Optional<Match> findByCandidate_UserIdAndJob_Id(UUID candidateId, UUID jobId)
    List<Match> findAllByCandidate_UserIdAndCompany_UserId(UUID candidateId, UUID companyId)

    @Modifying
    @Transactional
    @Query("""
            delete from Match m
            where m.candidate.userId = :candidateId
              and m.job.id = :jobId
            """)
    void deleteByCandidateIdAndJobId(UUID candidateId, UUID jobId)
}
