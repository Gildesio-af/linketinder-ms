package zg.acelera.msnucleo.repositories.country

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import zg.acelera.msnucleo.domain.Country

interface CountryRepository {
    Optional<Country> findById(UUID id)
    Page<Country> findAll(Pageable pageable)
    Optional<Country> findByNameIgnoreCase(String name)
    Page<Country> findByNameContainingIgnoreCase(String name, Pageable pageable)
    Optional<Country> findByCodeIgnoreCase(String code)
    Country save(Country country)
    boolean existsById(UUID id)
    void delete(UUID id)
}
