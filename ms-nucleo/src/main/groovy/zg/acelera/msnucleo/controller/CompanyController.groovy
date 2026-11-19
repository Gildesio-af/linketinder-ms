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
import zg.acelera.msnucleo.dto.company.CompanyCreateDTO
import zg.acelera.msnucleo.dto.company.CompanyResponseDTO
import zg.acelera.msnucleo.dto.company.CompanyUpdateDTO
import zg.acelera.msnucleo.service.CompanyService

@RestController
@RequestMapping("/companies")
class CompanyController {
    private final CompanyService companyService

    CompanyController(CompanyService companyService) {
        this.companyService = companyService
    }

    @GetMapping("/{id}")
    ResponseEntity<CompanyResponseDTO> findById(@PathVariable("id") UUID id) {
        ResponseEntity.ok(companyService.findById(id))
    }

    @GetMapping
    ResponseEntity<Page<CompanyResponseDTO>> findAll(Pageable pageable) {
        ResponseEntity.ok(companyService.findAll(pageable))
    }

    @PostMapping
    ResponseEntity<CompanyResponseDTO> save(@Valid @RequestBody CompanyCreateDTO dto) {
        CompanyResponseDTO saved = companyService.save(dto)
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saved.id)
                .toUri()
        ResponseEntity.created(location).body(saved)
    }

    @PutMapping("/{id}")
    ResponseEntity<CompanyResponseDTO> update(
            @PathVariable("id") UUID id,
            @Valid @RequestBody CompanyUpdateDTO dto) {
        ResponseEntity.ok(companyService.update(id, dto))
    }

    @DeleteMapping("/{id}")
    ResponseEntity<?> delete(@PathVariable("id") UUID id) {
        companyService.delete(id)
        ResponseEntity.noContent().build()
    }
}
