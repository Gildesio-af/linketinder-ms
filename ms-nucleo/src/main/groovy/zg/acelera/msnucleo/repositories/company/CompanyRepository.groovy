package zg.acelera.msnucleo.repositories.company

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import zg.acelera.msnucleo.domain.Company

interface CompanyRepository {
    Optional<Company> findById(UUID id)
    Page<Company> findAll(Pageable pageable)
    Optional<Company> findByCnpj(String cnpj)
    Company save(Company company)
    boolean existsById(UUID id)
    void delete(UUID id)
}
