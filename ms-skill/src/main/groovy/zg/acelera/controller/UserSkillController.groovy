package zg.acelera.controller

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import zg.acelera.dto.skill.SkillAssociationCreateDTO
import zg.acelera.dto.user_skill.UserSkillResponseDTO
import zg.acelera.dto.user_skill.UserSkillUpdateDTO
import zg.acelera.service.UserSkillService

@RestController
@RequestMapping("/users/skills")
class UserSkillController {
    private final UserSkillService userSkillService

    UserSkillController(UserSkillService userSkillService) {
        this.userSkillService = userSkillService
    }

    @GetMapping("/{userId}")
    ResponseEntity<List<UserSkillResponseDTO>> findByUserId(@PathVariable("userId") UUID userId) {
        return ResponseEntity.ok(userSkillService.findByUserId(userId))
    }

    @GetMapping("/{userId}/{skillId}")
    ResponseEntity<UserSkillResponseDTO> findById(@PathVariable("userId") UUID userId,
                                                  @PathVariable("skillId") UUID skillId) {
        return ResponseEntity.ok(userSkillService.findById(userId, skillId))
    }

    @PreAuthorize("hasRole('CANDIDATE')")
    @PostMapping
    ResponseEntity<UserSkillResponseDTO> save(@Valid @RequestBody SkillAssociationCreateDTO userSkillCreateDTO,
                                              Authentication auth) {
        UUID authenticatedUserId = (UUID) auth.principal
        UserSkillResponseDTO savedUserSkill = userSkillService.save(authenticatedUserId, userSkillCreateDTO)
        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/users/skills/{userId}/{skillId}")
                .buildAndExpand(savedUserSkill.userId, savedUserSkill.skillId)
                .toUri()
        return ResponseEntity.created(location).body(savedUserSkill)
    }

    @PreAuthorize("hasRole('CANDIDATE')")
    @PostMapping("/batch")
    ResponseEntity<List<UserSkillResponseDTO>> saveAll(@Valid @RequestBody List<SkillAssociationCreateDTO> skills,
                                                       Authentication auth) {
        UUID authenticatedUserId = (UUID) auth.principal
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userSkillService.saveAll(authenticatedUserId, skills))
    }

    @PreAuthorize("hasRole('CANDIDATE')")
    @PutMapping("/{skillId}")
    ResponseEntity<UserSkillResponseDTO> update(@PathVariable("skillId") UUID skillId,
                                                @Valid @RequestBody UserSkillUpdateDTO userSkillUpdateDTO,
                                                Authentication auth) {
        UUID authenticatedUserId = (UUID) auth.principal
        return ResponseEntity.ok(userSkillService.update(authenticatedUserId, skillId, userSkillUpdateDTO))
    }

    @PreAuthorize("hasRole('CANDIDATE')")
    @DeleteMapping("/{skillId}")
    ResponseEntity<?> delete(@PathVariable("skillId") UUID skillId,
                             Authentication auth) {
        UUID userId = (UUID) auth.principal
        userSkillService.delete(userId, skillId)
        return ResponseEntity.noContent().build()
    }
}
