package zg.acelera.msnucleo.repositories.company

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import zg.acelera.msnucleo.domain.CompanyLike
import zg.acelera.msnucleo.domain.CompanyLikeId

interface CompanyLikeRepository {
    Optional<CompanyLike> findById(CompanyLikeId id)
    Page<CompanyLike> findByCompanyId(UUID companyId, Pageable pageable)
    CompanyLike save(CompanyLike companyLike)
    boolean existsById(CompanyLikeId id)
    boolean existsByCompanyIdAndCandidateId(UUID companyId, UUID candidateId)
    void deleteById(CompanyLikeId id)
}
