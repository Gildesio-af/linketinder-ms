package zg.acelera.controller

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import zg.acelera.dto.user_skill.UserSkillCreateDTO
import zg.acelera.dto.user_skill.UserSkillResponseDTO
import zg.acelera.dto.user_skill.UserSkillUpdateDTO
import zg.acelera.service.UserSkillService

@RestController
@RequestMapping("/users/{userId}/skills")
class UserSkillController {
    private final UserSkillService userSkillService

    UserSkillController(UserSkillService userSkillService) {
        this.userSkillService = userSkillService
    }

    @GetMapping
    ResponseEntity<List<UserSkillResponseDTO>> findByUserId(@PathVariable("userId") UUID userId) {
        return ResponseEntity.ok(userSkillService.findByUserId(userId))
    }

    @GetMapping("/{skillId}")
    ResponseEntity<UserSkillResponseDTO> findById(
            @PathVariable("userId") UUID userId,
            @PathVariable("skillId") UUID skillId) {
        return ResponseEntity.ok(userSkillService.findById(userId, skillId))
    }

    @PostMapping
    ResponseEntity<UserSkillResponseDTO> save(
            @PathVariable("userId") UUID userId,
            @Valid @RequestBody UserSkillCreateDTO userSkillCreateDTO) {
        UserSkillResponseDTO savedUserSkill = userSkillService.save(userId, userSkillCreateDTO)
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{skillId}")
                .buildAndExpand(savedUserSkill.skillId)
                .toUri()
        return ResponseEntity.created(location).body(savedUserSkill)
    }

    @PutMapping("/{skillId}")
    ResponseEntity<UserSkillResponseDTO> update(
            @PathVariable("userId") UUID userId,
            @PathVariable("skillId") UUID skillId,
            @Valid @RequestBody UserSkillUpdateDTO userSkillUpdateDTO) {
        return ResponseEntity.ok(userSkillService.update(userId, skillId, userSkillUpdateDTO))
    }

    @DeleteMapping("/{skillId}")
    ResponseEntity<?> delete(
            @PathVariable("userId") UUID userId,
            @PathVariable("skillId") UUID skillId) {
        userSkillService.delete(userId, skillId)
        return ResponseEntity.noContent().build()
    }
}
