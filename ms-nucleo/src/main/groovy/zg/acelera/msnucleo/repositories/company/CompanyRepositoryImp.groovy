package zg.acelera.msnucleo.repositories.company

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import zg.acelera.msnucleo.domain.Company

@Repository
class CompanyRepositoryImp implements CompanyRepository {
    private final CompanyRepositoryJpa companyRepositoryJpa

    CompanyRepositoryImp(CompanyRepositoryJpa companyRepositoryJpa) {
        this.companyRepositoryJpa = companyRepositoryJpa
    }

    @Override Optional<Company> findById(UUID id) { companyRepositoryJpa.findById(id) }
    @Override Page<Company> findAll(Pageable pageable) { companyRepositoryJpa.findAll(pageable) }
    @Override Optional<Company> findByCnpj(String cnpj) { companyRepositoryJpa.findByCnpj(cnpj) }
    @Override Company save(Company company) { companyRepositoryJpa.save(company) }
    @Override boolean existsById(UUID id) { companyRepositoryJpa.existsById(id) }
    @Override void delete(UUID id) { companyRepositoryJpa.deleteById(id) }
}
