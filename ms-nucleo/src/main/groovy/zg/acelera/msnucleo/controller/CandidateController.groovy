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
import zg.acelera.msnucleo.dto.candidate.CandidateCreateDTO
import zg.acelera.msnucleo.dto.candidate.CandidateMatchResponseDTO
import zg.acelera.msnucleo.dto.candidate.CandidateResponseDTO
import zg.acelera.msnucleo.dto.candidate.CandidateUpdateDTO
import zg.acelera.msnucleo.service.CandidateService

@RestController
@RequestMapping("/candidates")
class CandidateController {
    private final CandidateService candidateService

    CandidateController(CandidateService candidateService) {
        this.candidateService = candidateService
    }

    @GetMapping("/{id}")
    ResponseEntity<CandidateMatchResponseDTO> findMatchedCandidate(@PathVariable("id") UUID id) {
        ResponseEntity.ok(candidateService.findMatchedCandidate(id))
    }

    @GetMapping
    ResponseEntity<Page<CandidateResponseDTO>> findAll(Pageable pageable) {
        ResponseEntity.ok(candidateService.findAll(pageable))
    }

    @PostMapping
    ResponseEntity<CandidateResponseDTO> save(@Valid @RequestBody CandidateCreateDTO dto) {
        CandidateResponseDTO saved = candidateService.save(dto)
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saved.id)
                .toUri()
        ResponseEntity.created(location).body(saved)
    }

    @PutMapping("/{id}")
    ResponseEntity<CandidateResponseDTO> update(
            @PathVariable("id") UUID id,
            @Valid @RequestBody CandidateUpdateDTO dto) {
        ResponseEntity.ok(candidateService.update(id, dto))
    }

    @DeleteMapping("/{id}")
    ResponseEntity<?> delete(@PathVariable("id") UUID id) {
        candidateService.delete(id)
        ResponseEntity.noContent().build()
    }
}
