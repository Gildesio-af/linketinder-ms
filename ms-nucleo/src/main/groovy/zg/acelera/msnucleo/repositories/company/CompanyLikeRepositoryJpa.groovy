package zg.acelera.msnucleo.repositories.company

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import zg.acelera.msnucleo.domain.CompanyLike
import zg.acelera.msnucleo.domain.CompanyLikeId

interface CompanyLikeRepositoryJpa extends JpaRepository<CompanyLike, CompanyLikeId> {
    Page<CompanyLike> findByIdCompanyId(UUID companyId, Pageable pageable)
}
