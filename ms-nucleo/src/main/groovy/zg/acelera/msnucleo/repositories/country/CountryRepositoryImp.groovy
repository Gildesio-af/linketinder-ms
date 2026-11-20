package zg.acelera.msnucleo.repositories.country

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import zg.acelera.msnucleo.domain.Country

@Repository
class CountryRepositoryImp implements CountryRepository {
    private final CountryRepositoryJpa countryRepositoryJpa

    CountryRepositoryImp(CountryRepositoryJpa countryRepositoryJpa) {
        this.countryRepositoryJpa = countryRepositoryJpa
    }

    @Override Optional<Country> findById(UUID id) { countryRepositoryJpa.findById(id) }
    @Override Page<Country> findAll(Pageable pageable) { countryRepositoryJpa.findAll(pageable) }
    @Override Optional<Country> findByNameIgnoreCase(String name) { countryRepositoryJpa.findByNameIgnoreCase(name) }
    @Override Page<Country> findByNameContainingIgnoreCase(String name, Pageable pageable) {
        countryRepositoryJpa.findByNameContainingIgnoreCase(name, pageable)
    }
    @Override Optional<Country> findByCodeIgnoreCase(String code) { countryRepositoryJpa.findByCodeIgnoreCase(code) }
    @Override Country save(Country country) { countryRepositoryJpa.save(country) }
    @Override boolean existsById(UUID id) { countryRepositoryJpa.existsById(id) }
    @Override void delete(UUID id) { countryRepositoryJpa.deleteById(id) }
}
