package zg.acelera.controller

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import zg.acelera.dto.job_skill.JobSkillCreateDTO
import zg.acelera.dto.job_skill.JobSkillResponseDTO
import zg.acelera.dto.job_skill.JobSkillUpdateDTO
import zg.acelera.service.JobSkillService

@RestController
@RequestMapping("/jobs/{jobId}/skills")
class JobSkillController {
    private final JobSkillService jobSkillService

    JobSkillController(JobSkillService jobSkillService) {
        this.jobSkillService = jobSkillService
    }

    @GetMapping
    ResponseEntity<List<JobSkillResponseDTO>> findByJobId(@PathVariable("jobId") UUID jobId) {
        return ResponseEntity.ok(jobSkillService.findByJobId(jobId))
    }

    @GetMapping("/{skillId}")
    ResponseEntity<JobSkillResponseDTO> findById(@PathVariable("jobId") UUID jobId,
                                                 @PathVariable("skillId") UUID skillId) {
        return ResponseEntity.ok(jobSkillService.findById(jobId, skillId))
    }

    @PreAuthorize("hasRole('COMPANY')")
    @PostMapping
    ResponseEntity<JobSkillResponseDTO> save(@PathVariable("jobId") UUID jobId,
                                             @Valid @RequestBody JobSkillCreateDTO jobSkillCreateDTO,
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
    @PutMapping("/{skillId}")
    ResponseEntity<JobSkillResponseDTO> update(@PathVariable("jobId") UUID jobId,
                                               @PathVariable("skillId") UUID skillId,
                                               @Valid @RequestBody JobSkillUpdateDTO jobSkillUpdateDTO,
                                               Authentication auth) {
        UUID companyId = (UUID) auth.principal
        return ResponseEntity.ok(jobSkillService.update(jobId, skillId, companyId, jobSkillUpdateDTO))
    }

    @PreAuthorize("hasRole('COMPANY')")
    @DeleteMapping("/{skillId}")
    ResponseEntity<?> delete(@PathVariable("jobId") UUID jobId,
                             @PathVariable("skillId") UUID skillId,
                             Authentication auth) {
        UUID companyId = (UUID) auth.principal
        jobSkillService.delete(jobId, skillId, companyId)
        return ResponseEntity.noContent().build()
    }
}
