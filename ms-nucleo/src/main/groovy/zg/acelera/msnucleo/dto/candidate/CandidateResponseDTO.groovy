package zg.acelera.msnucleo.dto.candidate

import zg.acelera.msnucleo.domain.Candidate
import zg.acelera.msnucleo.dto.address.AddressResponseDTO
import zg.acelera.msnucleo.dto.ms_skill.SkillResponseDTO

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
    List<SkillResponseDTO> skills = []

    static CandidateResponseDTO fromDomain(Candidate candidate,
                                           List<AddressResponseDTO> addresses = [],
                                           List<SkillResponseDTO> skills = []) {
        new CandidateResponseDTO(
                id: candidate.userId,
                name: candidate.user.name,
                email: candidate.user.email,
                description: candidate.user.description,
                cpf: candidate.cpf,
                lastName: candidate.lastName,
                birthDate: candidate.birthDate,
                addresses: addresses,
                skills: skills)
    }
}
