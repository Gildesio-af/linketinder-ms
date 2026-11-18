package zg.acelera.msnucleo.repositories.country

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import zg.acelera.msnucleo.domain.Country

interface CountryRepositoryJpa extends JpaRepository<Country, UUID> {
    Optional<Country> findByNameIgnoreCase(String name)
    Page<Country> findByNameContainingIgnoreCase(String name, Pageable pageable)
    Optional<Country> findByCodeIgnoreCase(String code)
}
