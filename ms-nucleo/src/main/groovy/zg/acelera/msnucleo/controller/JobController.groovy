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
import zg.acelera.msnucleo.dto.job.JobCreateDTO
import zg.acelera.msnucleo.dto.job.JobResponseDTO
import zg.acelera.msnucleo.dto.job.JobUpdateDTO
import zg.acelera.msnucleo.service.JobService

@RestController
@RequestMapping("/jobs")
class JobController {
    private final JobService jobService

    JobController(JobService jobService) {
        this.jobService = jobService
    }

    @GetMapping("/{id}")
    ResponseEntity<JobResponseDTO> findById(@PathVariable("id") UUID id) {
        ResponseEntity.ok(jobService.findById(id))
    }

    @GetMapping
    ResponseEntity<Page<JobResponseDTO>> findAll(Pageable pageable) {
        ResponseEntity.ok(jobService.findAll(pageable))
    }

    @GetMapping("/companies/{companyId}")
    ResponseEntity<Page<JobResponseDTO>> findByCompanyId(
            @PathVariable("companyId") UUID companyId,
            Pageable pageable) {
        ResponseEntity.ok(jobService.findByPublisherId(companyId, pageable))
    }

    @PostMapping
    ResponseEntity<JobResponseDTO> save(@Valid @RequestBody JobCreateDTO dto) {
        JobResponseDTO saved = jobService.save(dto)
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saved.id)
                .toUri()
        ResponseEntity.created(location).body(saved)
    }

    @PutMapping("/{id}")
    ResponseEntity<JobResponseDTO> update(
            @PathVariable("id") UUID id,
            @Valid @RequestBody JobUpdateDTO dto) {
        ResponseEntity.ok(jobService.update(id, dto))
    }

    @DeleteMapping("/{id}")
    ResponseEntity<?> delete(@PathVariable("id") UUID id) {
        jobService.delete(id)
        ResponseEntity.noContent().build()
    }
}
