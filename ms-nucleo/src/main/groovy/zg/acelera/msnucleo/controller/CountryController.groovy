package zg.acelera.msnucleo.controller

import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import zg.acelera.msnucleo.dto.country.CountryCreateDTO
import zg.acelera.msnucleo.dto.country.CountryResponseDTO
import zg.acelera.msnucleo.dto.country.CountryUpdateDTO
import zg.acelera.msnucleo.service.CountryService

@RestController
@RequestMapping("/countries")
class CountryController {
    private final CountryService countryService

    CountryController(CountryService countryService) {
        this.countryService = countryService
    }

    @GetMapping("/{id}")
    ResponseEntity<CountryResponseDTO> findById(@PathVariable("id") UUID id) {
        ResponseEntity.ok(countryService.findById(id))
    }

    @GetMapping
    ResponseEntity<Page<CountryResponseDTO>> findAll(Pageable pageable) {
        ResponseEntity.ok(countryService.findAll(pageable))
    }

    @GetMapping("/name/{name}")
    ResponseEntity<CountryResponseDTO> findByName(@PathVariable("name") String name) {
        ResponseEntity.ok(countryService.findByName(name))
    }

    @GetMapping("/code/{code}")
    ResponseEntity<CountryResponseDTO> findByCode(@PathVariable("code") String code) {
        ResponseEntity.ok(countryService.findByCode(code))
    }

    @PostMapping
    ResponseEntity<CountryResponseDTO> save(@Valid @RequestBody CountryCreateDTO dto) {
        CountryResponseDTO saved = countryService.save(dto)
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saved.id)
                .toUri()
        ResponseEntity.created(location).body(saved)
    }

    @PutMapping("/{id}")
    ResponseEntity<CountryResponseDTO> update(
            @PathVariable("id") UUID id,
            @Valid @RequestBody CountryUpdateDTO dto) {
        ResponseEntity.ok(countryService.update(id, dto))
    }

    @DeleteMapping("/{id}")
    ResponseEntity<?> delete(@PathVariable("id") UUID id) {
        countryService.delete(id)
        ResponseEntity.noContent().build()
    }
}
