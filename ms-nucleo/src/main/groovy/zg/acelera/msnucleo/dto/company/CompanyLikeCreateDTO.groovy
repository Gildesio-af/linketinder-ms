package zg.acelera.msnucleo.dto.company

import jakarta.validation.constraints.NotNull

class CompanyLikeCreateDTO {
    @NotNull(message = "Candidate id cannot be null")
    UUID candidateId
}
