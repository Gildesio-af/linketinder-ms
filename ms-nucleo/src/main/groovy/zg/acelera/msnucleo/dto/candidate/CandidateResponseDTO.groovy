package zg.acelera.msnucleo.dto.candidate

import zg.acelera.msnucleo.domain.Candidate
import zg.acelera.msnucleo.dto.address.AddressResponseDTO

import java.time.LocalDate

class CandidateResponseDTO {
    UUID id
    String name
    String email
    String description
    String cpf
    String lastName
    LocalDate birthDate
    List<AddressResponseDTO> addresses = []

    static CandidateResponseDTO fromDomain(Candidate candidate,
                                           List<AddressResponseDTO> addresses = []) {
        new CandidateResponseDTO(
                id: candidate.userId,
                name: candidate.user.name,
                email: candidate.user.email,
                description: candidate.user.description,
                cpf: candidate.cpf,
                lastName: candidate.lastName,
                birthDate: candidate.birthDate,
                addresses: addresses)
    }
}
