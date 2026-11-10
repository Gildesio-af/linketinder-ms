package zg.acelera.controller

import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import zg.acelera.dto.SkillCreateDTO
import zg.acelera.dto.SkillResponseDTO
import zg.acelera.service.SkillService

@RestController
@RequestMapping("/skills")
class SkillController {

    private final SkillService skillService

    SkillController(SkillService skillService) {
        this.skillService = skillService
    }

    @GetMapping("/{id}")
    ResponseEntity<SkillResponseDTO> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(skillService.findById(id))
    }

    @GetMapping
    ResponseEntity<Page<SkillResponseDTO>> findAll(Pageable pageable) {
        return ResponseEntity.ok(skillService.findAll(pageable))
    }

    @GetMapping("/name/{name}")
    ResponseEntity<SkillResponseDTO> findByName(@PathVariable String name) {
        return ResponseEntity.ok(skillService.findByNameIgnoreCase(name))
    }

    @PostMapping
    ResponseEntity<SkillResponseDTO> save(@Valid @RequestBody SkillCreateDTO skillCreateDTO) {
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(skillService.save(skillCreateDTO).id)
                .toUri()
        return ResponseEntity.created(location).body(skillService.save(skillCreateDTO))
    }

    @DeleteMapping("/{id}")
    ResponseEntity<?> delete(@PathVariable UUID id) {
        skillService.delete(id)
        return ResponseEntity.noContent().build()
    }
}
