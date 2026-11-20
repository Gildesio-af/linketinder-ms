package zg.acelera.msnucleo.repositories.match

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import zg.acelera.msnucleo.domain.Match

@Repository
class MatchRepositoryImp implements MatchRepository {
    private final MatchRepositoryJpa matchRepositoryJpa

    MatchRepositoryImp(MatchRepositoryJpa matchRepositoryJpa) {
        this.matchRepositoryJpa = matchRepositoryJpa
    }

    @Override Optional<Match> findById(UUID id) { matchRepositoryJpa.findById(id) }
    @Override Page<Match> findByCandidateId(UUID candidateId, Pageable pageable) {
        matchRepositoryJpa.findAllByCandidate_UserId(candidateId, pageable)
    }
    @Override Page<Match> findByCompanyId(UUID companyId, Pageable pageable) {
        matchRepositoryJpa.findAllByCompany_UserId(companyId, pageable)
    }
    @Override Optional<Match> findByCandidateIdAndJobId(UUID candidateId, UUID jobId) {
        matchRepositoryJpa.findByCandidate_UserIdAndJob_Id(candidateId, jobId)
    }
    @Override List<Match> findByCandidateIdAndCompanyId(UUID candidateId, UUID companyId) {
        matchRepositoryJpa.findAllByCandidate_UserIdAndCompany_UserId(candidateId, companyId)
    }
    @Override Match save(Match match) { matchRepositoryJpa.save(match) }
    @Override void delete(Match match) { matchRepositoryJpa.delete(match) }
    @Override void deleteByCandidateIdAndJobId(UUID candidateId, UUID jobId) {
        matchRepositoryJpa.deleteByCandidateIdAndJobId(candidateId, jobId)
    }
}
