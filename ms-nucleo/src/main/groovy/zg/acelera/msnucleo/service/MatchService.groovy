package zg.acelera.msnucleo.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import zg.acelera.msnucleo.domain.Candidate
import zg.acelera.msnucleo.domain.Company
import zg.acelera.msnucleo.domain.Job
import zg.acelera.msnucleo.domain.Match
import zg.acelera.msnucleo.dto.match.MatchResponseDTO
import zg.acelera.msnucleo.repositories.company.CompanyLikeRepository
import zg.acelera.msnucleo.repositories.job.JobLikeRepository
import zg.acelera.msnucleo.repositories.match.MatchRepository
import zg.acelera.msnucleo.utils.exception.EntityNotFoundException

import java.time.LocalDateTime

@Service
class MatchService {
    private final MatchRepository matchRepository
    private final JobLikeRepository jobLikeRepository
    private final CompanyLikeRepository companyLikeRepository

    MatchService(MatchRepository matchRepository,
                 JobLikeRepository jobLikeRepository,
                 CompanyLikeRepository companyLikeRepository) {
        this.matchRepository = matchRepository
        this.jobLikeRepository = jobLikeRepository
        this.companyLikeRepository = companyLikeRepository
    }

    @Transactional(readOnly = true)
    Page<MatchResponseDTO> findByCandidateId(UUID candidateId, Pageable pageable) {
        matchRepository.findByCandidateId(candidateId, pageable)
                .map { MatchResponseDTO.fromDomain(it) }
    }

    @Transactional(readOnly = true)
    Page<MatchResponseDTO> findByCompanyId(UUID companyId, Pageable pageable) {
        matchRepository.findByCompanyId(companyId, pageable)
                .map { MatchResponseDTO.fromDomain(it) }
    }

    @Transactional(readOnly = true)
    MatchResponseDTO findById(UUID id) {
        Match match = matchRepository.findById(id)
                .orElseThrow { new EntityNotFoundException("Match not found with id: $id") }
        MatchResponseDTO.fromDomain(match)
    }

    @Transactional
    void createMatchesForCompanyLike(Company company, Candidate candidate) {
        jobLikeRepository.findAllByCandidateId(candidate.userId).each { jobLike ->
            Job job = jobLike.job
            if (job.publisher.userId == company.userId)
                createIfAbsent(candidate, company, job)
        }
    }

    @Transactional
    void createMatchForJobLike(Candidate candidate, Job job) {
        UUID companyId = job.publisher.userId
        if (companyLikeRepository.existsByCompanyIdAndCandidateId(companyId, candidate.userId))
            createIfAbsent(candidate, job.publisher, job)
    }

    @Transactional
    void deleteMatchesByCandidateAndCompany(UUID candidateId, UUID companyId) {
        matchRepository.findByCandidateIdAndCompanyId(candidateId, companyId)
                .each { matchRepository.delete(it) }
    }

    @Transactional
    void deleteMatchByCandidateAndJob(UUID candidateId, UUID jobId) {
        matchRepository.deleteByCandidateIdAndJobId(candidateId, jobId)
    }

    private void createIfAbsent(Candidate candidate, Company company, Job job) {
        matchRepository.findByCandidateIdAndJobId(candidate.userId, job.id)
                .orElseGet {
                    matchRepository.save(new Match(
                            candidate: candidate,
                            company: company,
                            job: job,
                            createdAt: LocalDateTime.now()))
                }
    }
}
