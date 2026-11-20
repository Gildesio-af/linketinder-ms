package zg.acelera.msnucleo.repositories.company

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import zg.acelera.msnucleo.domain.CompanyLike
import zg.acelera.msnucleo.domain.CompanyLikeId

@Repository
class CompanyLikeRepositoryImp implements CompanyLikeRepository {
    private final CompanyLikeRepositoryJpa companyLikeRepositoryJpa

    CompanyLikeRepositoryImp(CompanyLikeRepositoryJpa companyLikeRepositoryJpa) {
        this.companyLikeRepositoryJpa = companyLikeRepositoryJpa
    }

    @Override Optional<CompanyLike> findById(CompanyLikeId id) { companyLikeRepositoryJpa.findById(id) }
    @Override Page<CompanyLike> findByCompanyId(UUID companyId, Pageable pageable) {
        companyLikeRepositoryJpa.findByIdCompanyId(companyId, pageable)
    }
    @Override CompanyLike save(CompanyLike companyLike) { companyLikeRepositoryJpa.save(companyLike) }
    @Override boolean existsById(CompanyLikeId id) { companyLikeRepositoryJpa.existsById(id) }
    @Override void deleteById(CompanyLikeId id) { companyLikeRepositoryJpa.deleteById(id) }
}
