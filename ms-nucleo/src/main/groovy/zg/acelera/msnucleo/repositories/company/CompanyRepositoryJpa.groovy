package zg.acelera.msnucleo.repositories.company

import org.springframework.data.jpa.repository.JpaRepository
import zg.acelera.msnucleo.domain.Company

interface CompanyRepositoryJpa extends JpaRepository<Company, UUID> {
    Optional<Company> findByCnpj(String cnpj)
}
