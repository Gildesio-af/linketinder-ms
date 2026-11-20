package zg.acelera.msnucleo.repositories.candidate

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import zg.acelera.msnucleo.domain.Candidate

@Repository
class CandidateRepositoryImp implements CandidateRepository {
    private final CandidateRepositoryJpa candidateRepositoryJpa

    CandidateRepositoryImp(CandidateRepositoryJpa candidateRepositoryJpa) {
        this.candidateRepositoryJpa = candidateRepositoryJpa
    }

    @Override Optional<Candidate> findById(UUID id) { candidateRepositoryJpa.findById(id) }
    @Override Page<Candidate> findAll(Pageable pageable) { candidateRepositoryJpa.findAll(pageable) }
    @Override Optional<Candidate> findByCpf(String cpf) { candidateRepositoryJpa.findByCpf(cpf) }
    @Override Candidate save(Candidate candidate) { candidateRepositoryJpa.save(candidate) }
    @Override boolean existsById(UUID id) { candidateRepositoryJpa.existsById(id) }
    @Override void delete(UUID id) { candidateRepositoryJpa.deleteById(id) }
}
