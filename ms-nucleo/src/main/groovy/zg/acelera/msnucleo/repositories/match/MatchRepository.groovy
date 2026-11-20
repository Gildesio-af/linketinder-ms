package zg.acelera.msnucleo.repositories.match

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import zg.acelera.msnucleo.domain.Match

interface MatchRepository {
    Optional<Match> findById(UUID id)
    Page<Match> findByCandidateId(UUID candidateId, Pageable pageable)
    Page<Match> findByCompanyId(UUID companyId, Pageable pageable)
    Optional<Match> findByCandidateIdAndJobId(UUID candidateId, UUID jobId)
    List<Match> findByCandidateIdAndCompanyId(UUID candidateId, UUID companyId)
    Match save(Match match)
    void delete(Match match)
    void deleteByCandidateIdAndJobId(UUID candidateId, UUID jobId)
}
