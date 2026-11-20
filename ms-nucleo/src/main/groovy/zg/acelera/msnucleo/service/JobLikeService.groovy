package zg.acelera.msnucleo.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import zg.acelera.msnucleo.domain.Candidate
import zg.acelera.msnucleo.domain.Job
import zg.acelera.msnucleo.domain.JobLike
import zg.acelera.msnucleo.domain.JobLikeId
import zg.acelera.msnucleo.dto.job.JobLikeResponseDTO
import zg.acelera.msnucleo.repositories.candidate.CandidateRepository
import zg.acelera.msnucleo.repositories.job.JobLikeRepository
import zg.acelera.msnucleo.repositories.job.JobRepository
import zg.acelera.msnucleo.utils.exception.EntityNotFoundException

@Service
class JobLikeService {
    private final JobLikeRepository jobLikeRepository
    private final CandidateRepository candidateRepository
    private final JobRepository jobRepository
    private final MatchService matchService

    JobLikeService(JobLikeRepository jobLikeRepository,
                   CandidateRepository candidateRepository,
                   JobRepository jobRepository,
                   MatchService matchService) {
        this.jobLikeRepository = jobLikeRepository
        this.candidateRepository = candidateRepository
        this.jobRepository = jobRepository
        this.matchService = matchService
    }

    @Transactional(readOnly = true)
    Page<JobLikeResponseDTO> findAll(UUID candidateId, Pageable pageable) {
        jobLikeRepository.findByCandidateId(candidateId, pageable)
                .map { JobLikeResponseDTO.fromDomain(it) }
    }

    @Transactional
    JobLikeResponseDTO save(UUID candidateId, UUID jobId) {
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow { new EntityNotFoundException("Candidate not found with id: $candidateId") }
        Job job = jobRepository.findById(jobId)
                .orElseThrow { new EntityNotFoundException("Job not found with id: $jobId") }

        JobLikeId likeId = new JobLikeId(
                candidateId: candidateId,
                jobId: jobId)
        if (jobLikeRepository.existsById(likeId))
            throw new IllegalArgumentException("Candidate already liked job with id: $jobId")

        JobLike jobLike = new JobLike(
                id: likeId,
                candidate: candidate,
                job: job)
        JobLikeResponseDTO response =
                JobLikeResponseDTO.fromDomain(jobLikeRepository.save(jobLike))
        matchService.createMatchForJobLike(candidate, job)
        response
    }

    @Transactional
    void delete(UUID candidateId, UUID jobId) {
        JobLikeId likeId = new JobLikeId(
                candidateId: candidateId,
                jobId: jobId)
        if (!jobLikeRepository.existsById(likeId))
            throw new EntityNotFoundException("Job like not found")
        jobLikeRepository.deleteById(likeId)
        matchService.deleteMatchByCandidateAndJob(candidateId, jobId)
    }
}
