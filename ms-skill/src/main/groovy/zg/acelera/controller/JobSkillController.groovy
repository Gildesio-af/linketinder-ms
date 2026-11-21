package zg.acelera.controller

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import zg.acelera.dto.skill.SkillAssociationCreateDTO
import zg.acelera.dto.job_skill.JobSkillResponseDTO
import zg.acelera.dto.job_skill.JobSkillUpdateDTO
import zg.acelera.service.JobSkillService

@RestController
@RequestMapping("/jobs")
class JobSkillController {
    private final JobSkillService jobSkillService

    JobSkillController(JobSkillService jobSkillService) {
        this.jobSkillService = jobSkillService
    }

    @GetMapping("/skills/batch")
    ResponseEntity<Map<UUID, List<JobSkillResponseDTO>>> findByJobIds(
            @RequestParam(name = "jobIds", defaultValue = "") List<UUID> jobIds) {
        return ResponseEntity.ok(jobSkillService.findByJobIds(jobIds))
    }

    @GetMapping("/{jobId}/skills")
    ResponseEntity<List<JobSkillResponseDTO>> findByJobId(@PathVariable("jobId") UUID jobId) {
        return ResponseEntity.ok(jobSkillService.findByJobId(jobId))
    }

    @GetMapping("/{jobId}/skills/{skillId}")
    ResponseEntity<JobSkillResponseDTO> findById(@PathVariable("jobId") UUID jobId,
                                                 @PathVariable("skillId") UUID skillId) {
        return ResponseEntity.ok(jobSkillService.findById(jobId, skillId))
    }

    @PreAuthorize("hasRole('COMPANY')")
    @PostMapping("/{jobId}/skills")
    ResponseEntity<JobSkillResponseDTO> save(@PathVariable("jobId") UUID jobId,
                                             @Valid @RequestBody SkillAssociationCreateDTO jobSkillCreateDTO,
                                             Authentication auth) {
        UUID companyId = (UUID) auth.principal
        JobSkillResponseDTO savedJobSkill = jobSkillService.save(jobId, companyId, jobSkillCreateDTO)
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{skillId}")
                .buildAndExpand(savedJobSkill.skillId)
                .toUri()
        return ResponseEntity.created(location).body(savedJobSkill)
    }

    @PreAuthorize("hasRole('COMPANY')")
    @PostMapping("/{jobId}/skills/batch")
    ResponseEntity<List<JobSkillResponseDTO>> saveAll(@PathVariable("jobId") UUID jobId,
                                                      @Valid @RequestBody List<SkillAssociationCreateDTO> skills,
                                                      Authentication auth) {
        UUID companyId = (UUID) auth.principal
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(jobSkillService.saveAll(jobId, companyId, skills))
    }

    @PreAuthorize("hasRole('COMPANY')")
    @PutMapping("/{jobId}/skills/{skillId}")
    ResponseEntity<JobSkillResponseDTO> update(@PathVariable("jobId") UUID jobId,
                                               @PathVariable("skillId") UUID skillId,
                                               @Valid @RequestBody JobSkillUpdateDTO jobSkillUpdateDTO,
                                               Authentication auth) {
        UUID companyId = (UUID) auth.principal
        return ResponseEntity.ok(jobSkillService.update(jobId, skillId, companyId, jobSkillUpdateDTO))
    }

    @PreAuthorize("hasRole('COMPANY')")
    @DeleteMapping("/{jobId}/skills/{skillId}")
    ResponseEntity<?> delete(@PathVariable("jobId") UUID jobId,
                             @PathVariable("skillId") UUID skillId,
                             Authentication auth) {
        UUID companyId = (UUID) auth.principal
        jobSkillService.delete(jobId, skillId, companyId)
        return ResponseEntity.noContent().build()
    }
}
