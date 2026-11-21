package zg.acelera.msnucleo.dto.candidate

import zg.acelera.msnucleo.domain.Candidate
import zg.acelera.msnucleo.dto.ms_skill.SkillResponseDTO

import java.time.LocalDate

class CandidateMatchResponseDTO {
    UUID id
    String name
    String email
    String description
    String lastName
    LocalDate birthDate
    List<SkillResponseDTO> skills = []

    static CandidateMatchResponseDTO fromDomain(Candidate candidate,
                                                List<SkillResponseDTO> skills = []) {
        new CandidateMatchResponseDTO(
                id: candidate.userId,
                name: candidate.user.name,
                email: candidate.user.email,
                description: candidate.user.description,
                lastName: candidate.lastName,
                birthDate: candidate.birthDate,
                skills: skills)
    }
}
