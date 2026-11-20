package zg.acelera.msnucleo.controller

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import zg.acelera.msnucleo.dto.job.JobLikeResponseDTO
import zg.acelera.msnucleo.infrastructure.security.CustomUserDetails
import zg.acelera.msnucleo.service.JobLikeService

@RestController
@RequestMapping("/jobs/likes")
class JobLikeController {
    private final JobLikeService jobLikeService

    JobLikeController(JobLikeService jobLikeService) {
        this.jobLikeService = jobLikeService
    }

    @GetMapping
    @PreAuthorize("hasRole('CANDIDATE')")
    ResponseEntity<Page<JobLikeResponseDTO>> findAll(
            @AuthenticationPrincipal CustomUserDetails principal,
            Pageable pageable) {
        ResponseEntity.ok(jobLikeService.findAll(getCandidateId(principal), pageable))
    }

    @PostMapping("/{jobId}")
    @PreAuthorize("hasRole('CANDIDATE')")
    ResponseEntity<JobLikeResponseDTO> save(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable("jobId") UUID jobId) {
        ResponseEntity.status(HttpStatus.CREATED)
                .body(jobLikeService.save(getCandidateId(principal), jobId))
    }

    @DeleteMapping("/{jobId}")
    @PreAuthorize("hasRole('CANDIDATE')")
    ResponseEntity<?> delete(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable("jobId") UUID jobId) {
        jobLikeService.delete(getCandidateId(principal), jobId)
        ResponseEntity.noContent().build()
    }

    private static UUID getCandidateId(CustomUserDetails principal) {
        UUID candidateId = principal?.user?.id
        if (candidateId == null)
            throw new IllegalArgumentException("Authenticated candidate id not found")
        candidateId
    }
}
