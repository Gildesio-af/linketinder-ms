package zg.acelera.msnucleo.repositories.candidate

import org.springframework.data.jpa.repository.JpaRepository
import zg.acelera.msnucleo.domain.Candidate

interface CandidateRepositoryJpa extends JpaRepository<Candidate, UUID> {
    Optional<Candidate> findByCpf(String cpf)
}
