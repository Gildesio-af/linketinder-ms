package zg.acelera.msnucleo.dto.match

import zg.acelera.msnucleo.domain.Match

import java.time.LocalDateTime

class MatchResponseDTO {
    UUID id
    UUID candidateId
    UUID companyId
    UUID jobId
    LocalDateTime createdAt

    static MatchResponseDTO fromDomain(Match match) {
        new MatchResponseDTO(
                id: match.id,
                candidateId: match.candidate.userId,
                companyId: match.company.userId,
                jobId: match.job.id,
                createdAt: match.createdAt)
    }
}
