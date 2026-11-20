package zg.acelera.msnucleo.controller


import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

import zg.acelera.msnucleo.dto.company.CompanyLikeResponseDTO
import zg.acelera.msnucleo.infrastructure.security.CustomUserDetails
import zg.acelera.msnucleo.service.CompanyLikeService

@RestController
@RequestMapping("/companies/likes")
class CompanyLikeController {
    private final CompanyLikeService companyLikeService

    CompanyLikeController(CompanyLikeService companyLikeService) {
        this.companyLikeService = companyLikeService
    }

    @GetMapping
    @PreAuthorize("hasRole('COMPANY')")
    ResponseEntity<Page<CompanyLikeResponseDTO>> findAll(
            @AuthenticationPrincipal CustomUserDetails principal,
            Pageable pageable) {
        ResponseEntity.ok(companyLikeService.findAll(getCompanyId(principal), pageable))
    }

    @PostMapping("/{candidateId}")
    @PreAuthorize("hasRole('COMPANY')")
    ResponseEntity<CompanyLikeResponseDTO> saveByCandidateId(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable("candidateId") UUID candidateId) {
        ResponseEntity.status(HttpStatus.CREATED)
                .body(companyLikeService.save(getCompanyId(principal), candidateId))
    }

    @DeleteMapping("/{candidateId}")
    @PreAuthorize("hasRole('COMPANY')")
    ResponseEntity<?> delete(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable("candidateId") UUID candidateId) {
        companyLikeService.delete(getCompanyId(principal), candidateId)
        ResponseEntity.noContent().build()
    }

    private static UUID getCompanyId(CustomUserDetails principal) {
        UUID companyId = principal?.user?.id
        if (companyId == null)
            throw new IllegalArgumentException("Authenticated company id not found")
        companyId
    }
}
