package zg.acelera.msnucleo.dto.candidate

import zg.acelera.msnucleo.domain.Candidate

import java.time.LocalDate

class CandidateMatchResponseDTO {
    UUID id
    String name
    String email
    String description
    String lastName
    LocalDate birthDate

    static CandidateMatchResponseDTO fromDomain(Candidate candidate) {
        new CandidateMatchResponseDTO(
                id: candidate.userId,
                name: candidate.user.name,
                email: candidate.user.email,
                description: candidate.user.description,
                lastName: candidate.lastName,
                birthDate: candidate.birthDate)
    }
}
