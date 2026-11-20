package zg.acelera.msnucleo.service

import spock.lang.Specification
import zg.acelera.msnucleo.domain.Candidate
import zg.acelera.msnucleo.domain.Company
import zg.acelera.msnucleo.domain.CompanyLike
import zg.acelera.msnucleo.domain.CompanyLikeId

import zg.acelera.msnucleo.dto.company.CompanyLikeResponseDTO
import zg.acelera.msnucleo.repositories.candidate.CandidateRepository
import zg.acelera.msnucleo.repositories.company.CompanyLikeRepository
import zg.acelera.msnucleo.repositories.company.CompanyRepository

class CompanyLikeServiceTest extends Specification {
    CompanyLikeRepository companyLikeRepository
    CompanyRepository companyRepository
    CandidateRepository candidateRepository
    MatchService matchService
    CompanyLikeService service

    def setup() {
        companyLikeRepository = Mock()
        companyRepository = Mock()
        candidateRepository = Mock()
        matchService = Mock()
        service = new CompanyLikeService(
                companyLikeRepository, companyRepository, candidateRepository, matchService)
    }

    void "company likes candidate using authenticated company id"() {
        given:
        UUID companyId = UUID.randomUUID()
        UUID candidateId = UUID.randomUUID()
        Company company = new Company(userId: companyId)
        Candidate candidate = new Candidate(userId: candidateId)
        companyRepository.findById(companyId) >> Optional.of(company)
        candidateRepository.findById(candidateId) >> Optional.of(candidate)
        companyLikeRepository.existsById({ CompanyLikeId id ->
            id.companyId == companyId && id.likedCandidateId == candidateId
        } as CompanyLikeId) >> false

        when:
        CompanyLikeResponseDTO result = service.save(
                companyId,
                candidateId)

        then:
        1 * companyLikeRepository.save({
            CompanyLike like -> like.id.companyId == companyId &&
                    like.id.likedCandidateId == candidateId &&
                    like.company == company &&
                    like.candidate == candidate
        }) >> { CompanyLike like -> like }
        result.companyId == companyId
        result.candidateId == candidateId
        1 * matchService.createMatchesForCompanyLike(company, candidate)
    }

    void "delete removes only authenticated company like"() {
        given:
        UUID companyId = UUID.randomUUID()
        UUID candidateId = UUID.randomUUID()
        companyLikeRepository.existsById({ CompanyLikeId id ->
            id.companyId == companyId && id.likedCandidateId == candidateId
        } as CompanyLikeId) >> true

        when:
        service.delete(companyId, candidateId)

        then:
        1 * companyLikeRepository.deleteById({
            CompanyLikeId id -> id.companyId == companyId &&
                    id.likedCandidateId == candidateId
        })
        1 * matchService.deleteMatchesByCandidateAndCompany(candidateId, companyId)
    }

}
