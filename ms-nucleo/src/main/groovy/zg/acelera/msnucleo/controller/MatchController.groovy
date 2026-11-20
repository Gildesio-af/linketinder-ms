package zg.acelera.msnucleo.controller

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import zg.acelera.msnucleo.dto.match.MatchResponseDTO
import zg.acelera.msnucleo.infrastructure.security.CustomUserDetails
import zg.acelera.msnucleo.service.MatchService

@RestController
@RequestMapping("/matches")
class MatchController {
    private final MatchService matchService

    MatchController(MatchService matchService) {
        this.matchService = matchService
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('CANDIDATE','COMPANY')")
    ResponseEntity<Page<MatchResponseDTO>> findAll(
            @AuthenticationPrincipal CustomUserDetails principal,
            Pageable pageable) {
        UUID userId = principal.user.id
        String role = principal.user.role.name()
        if (role == 'CANDIDATE')
            ResponseEntity.ok(matchService.findByCandidateId(userId, pageable))
        else
            ResponseEntity.ok(matchService.findByCompanyId(userId, pageable))
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CANDIDATE','COMPANY')")
    ResponseEntity<MatchResponseDTO> findById(@PathVariable("id") UUID id) {
        ResponseEntity.ok(matchService.findById(id))
    }
}
