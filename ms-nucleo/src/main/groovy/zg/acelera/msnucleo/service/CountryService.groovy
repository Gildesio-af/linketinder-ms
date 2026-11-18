package zg.acelera.msnucleo.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import zg.acelera.msnucleo.domain.Country
import zg.acelera.msnucleo.dto.country.CountryCreateDTO
import zg.acelera.msnucleo.dto.country.CountryResponseDTO
import zg.acelera.msnucleo.dto.country.CountryUpdateDTO
import zg.acelera.msnucleo.repositories.country.CountryRepository
import zg.acelera.msnucleo.utils.exception.EntityNotFoundException

@Service
class CountryService {
    private final CountryRepository countryRepository

    CountryService(CountryRepository countryRepository) {
        this.countryRepository = countryRepository
    }

    @Transactional(readOnly = true)
    CountryResponseDTO findById(UUID id) {
        Country country = findEntity(id)
        CountryResponseDTO.fromDomain(country)
    }

    @Transactional(readOnly = true)
    Page<CountryResponseDTO> findAll(Pageable pageable) {
        countryRepository.findAll(pageable).map { CountryResponseDTO.fromDomain(it) }
    }

    @Transactional(readOnly = true)
    CountryResponseDTO findByName(String name) {
        Country country = countryRepository.findByNameIgnoreCase(name.trim())
                .orElseThrow { new EntityNotFoundException("Country not found with name: $name") }
        CountryResponseDTO.fromDomain(country)
    }

    @Transactional(readOnly = true)
    CountryResponseDTO findByCode(String code) {
        Country country = countryRepository.findByCodeIgnoreCase(code.trim())
                .orElseThrow { new EntityNotFoundException("Country not found with code: ${code.trim()}") }
        CountryResponseDTO.fromDomain(country)
    }

    @Transactional
    CountryResponseDTO save(CountryCreateDTO dto) {
        Country country = dto.toDomain()
        validateUnique(country.name, country.code, null)
        CountryResponseDTO.fromDomain(countryRepository.save(country))
    }

    @Transactional
    CountryResponseDTO update(UUID id, CountryUpdateDTO dto) {
        Country country = findEntity(id)
        String name = dto.name().trim()
        String code = dto.code().trim().toUpperCase()
        validateUnique(name, code, id)
        country.name = name
        country.code = code
        CountryResponseDTO.fromDomain(countryRepository.save(country))
    }

    @Transactional
    void delete(UUID id) {
        if (!countryRepository.existsById(id))
            throw new EntityNotFoundException("Country not found with id: $id")
        countryRepository.delete(id)
    }

    private Country findEntity(UUID id) {
        countryRepository.findById(id)
                .orElseThrow { new EntityNotFoundException("Country not found with id: $id") }
    }

    private void validateUnique(String name, String code, UUID ignoredId) {
        if (countryRepository.findByNameIgnoreCase(name).isPresent()) {
            Country existing = countryRepository.findByNameIgnoreCase(name).get()
            if (existing.id != ignoredId)
                throw new IllegalArgumentException("Country with name '$name' already exists")
        }
        if (countryRepository.findByCodeIgnoreCase(code).isPresent()) {
            Country existing = countryRepository.findByCodeIgnoreCase(code).get()
            if (existing.id != ignoredId)
                throw new IllegalArgumentException("Country with code '$code' already exists")
        }
    }
}
