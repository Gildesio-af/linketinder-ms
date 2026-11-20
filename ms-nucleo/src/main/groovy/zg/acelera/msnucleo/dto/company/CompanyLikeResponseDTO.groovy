package zg.acelera.msnucleo.dto.company

import zg.acelera.msnucleo.domain.CompanyLike

class CompanyLikeResponseDTO {
    UUID companyId
    UUID candidateId

    static CompanyLikeResponseDTO fromDomain(CompanyLike companyLike) {
        new CompanyLikeResponseDTO(
                companyId: companyLike.id.companyId,
                candidateId: companyLike.id.likedCandidateId)
    }
}
