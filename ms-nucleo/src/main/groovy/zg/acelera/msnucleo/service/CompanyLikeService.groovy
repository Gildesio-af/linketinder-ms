package zg.acelera.msnucleo.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import zg.acelera.msnucleo.domain.Candidate
import zg.acelera.msnucleo.domain.Company
import zg.acelera.msnucleo.domain.CompanyLike
import zg.acelera.msnucleo.domain.CompanyLikeId

import zg.acelera.msnucleo.dto.company.CompanyLikeResponseDTO
import zg.acelera.msnucleo.repositories.candidate.CandidateRepository
import zg.acelera.msnucleo.repositories.company.CompanyLikeRepository
import zg.acelera.msnucleo.repositories.company.CompanyRepository
import zg.acelera.msnucleo.utils.exception.EntityNotFoundException

@Service
class CompanyLikeService {
    private final CompanyLikeRepository companyLikeRepository
    private final CompanyRepository companyRepository
    private final CandidateRepository candidateRepository
    private final MatchService matchService

    CompanyLikeService(CompanyLikeRepository companyLikeRepository,
                       CompanyRepository companyRepository,
                       CandidateRepository candidateRepository,
                       MatchService matchService) {
        this.companyLikeRepository = companyLikeRepository
        this.companyRepository = companyRepository
        this.candidateRepository = candidateRepository
        this.matchService = matchService
    }

    @Transactional(readOnly = true)
    Page<CompanyLikeResponseDTO> findAll(UUID companyId, Pageable pageable) {
        companyLikeRepository.findByCompanyId(companyId, pageable)
                .map { CompanyLikeResponseDTO.fromDomain(it) }
    }

    @Transactional
    CompanyLikeResponseDTO save(UUID companyId, UUID candidateId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow { new EntityNotFoundException("Company not found with id: $companyId") }
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow { new EntityNotFoundException("Candidate not found with id: $candidateId") }

        CompanyLikeId likeId = new CompanyLikeId(
                companyId: companyId,
                likedCandidateId: candidateId)
        if (companyLikeRepository.existsById(likeId))
            throw new IllegalArgumentException("Company already liked candidate with id: $candidateId")

        CompanyLike companyLike = new CompanyLike(
                id: likeId,
                company: company,
                candidate: candidate)
        CompanyLikeResponseDTO response =
                CompanyLikeResponseDTO.fromDomain(companyLikeRepository.save(companyLike))
        matchService.createMatchesForCompanyLike(company, candidate)
        response
    }

    @Transactional
    void delete(UUID companyId, UUID candidateId) {
        CompanyLikeId likeId = new CompanyLikeId(
                companyId: companyId,
                likedCandidateId: candidateId)
        if (!companyLikeRepository.existsById(likeId))
            throw new EntityNotFoundException("Company like not found")
        companyLikeRepository.deleteById(likeId)
        matchService.deleteMatchesByCandidateAndCompany(candidateId, companyId)
    }
}
