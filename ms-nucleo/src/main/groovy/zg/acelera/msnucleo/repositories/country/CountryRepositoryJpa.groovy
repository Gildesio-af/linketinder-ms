package zg.acelera.msnucleo.repositories.country

import org.springframework.data.jpa.repository.JpaRepository
import zg.acelera.msnucleo.domain.Country

interface CountryRepositoryJpa extends JpaRepository<Country, UUID> {
    Optional<Country> findByNameIgnoreCase(String name)
    Optional<Country> findByCodeIgnoreCase(String code)
}
