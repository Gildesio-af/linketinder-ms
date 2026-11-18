package zg.acelera.msnucleo.repositories.candidate

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import zg.acelera.msnucleo.domain.Candidate

interface CandidateRepository {
    Optional<Candidate> findById(UUID id)
    Page<Candidate> findAll(Pageable pageable)
    Optional<Candidate> findByCpf(String cpf)
    Candidate save(Candidate candidate)
    boolean existsById(UUID id)
    void delete(UUID id)
}
