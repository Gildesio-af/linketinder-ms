package zg.acelera.msnucleo.service

import spock.lang.Specification
import zg.acelera.msnucleo.domain.Candidate
import zg.acelera.msnucleo.domain.Job
import zg.acelera.msnucleo.domain.JobLike
import zg.acelera.msnucleo.domain.JobLikeId
import zg.acelera.msnucleo.dto.job.JobLikeResponseDTO
import zg.acelera.msnucleo.repositories.candidate.CandidateRepository
import zg.acelera.msnucleo.repositories.job.JobLikeRepository
import zg.acelera.msnucleo.repositories.job.JobRepository

class JobLikeServiceTest extends Specification {
    JobLikeRepository jobLikeRepository
    CandidateRepository candidateRepository
    JobRepository jobRepository
    JobLikeService service

    def setup() {
        jobLikeRepository = Mock()
        candidateRepository = Mock()
        jobRepository = Mock()
        service = new JobLikeService(
                jobLikeRepository,
                candidateRepository,
                jobRepository)
    }

    void "candidate likes job using candidate id"() {
        given:
        UUID candidateId = UUID.randomUUID()
        UUID jobId = UUID.randomUUID()
        Candidate candidate = new Candidate(userId: candidateId)
        Job job = new Job(id: jobId)
        candidateRepository.findById(candidateId) >> Optional.of(candidate)
        jobRepository.findById(jobId) >> Optional.of(job)
        jobLikeRepository.existsById({ JobLikeId id ->
            id.candidateId == candidateId && id.jobId == jobId
        }) >> false

        when:
        JobLikeResponseDTO result = service.save(candidateId, jobId)

        then:
        1 * jobLikeRepository.save({
            JobLike like -> like.id.candidateId == candidateId &&
                    like.id.jobId == jobId &&
                    like.candidate == candidate &&
                    like.job == job
        }) >> { JobLike like -> like }
        result.candidateId == candidateId
        result.jobId == jobId
    }

    void "rejects duplicate job like"() {
        given:
        UUID candidateId = UUID.randomUUID()
        UUID jobId = UUID.randomUUID()
        candidateRepository.findById(candidateId) >>
                Optional.of(new Candidate(userId: candidateId))
        jobRepository.findById(jobId) >> Optional.of(new Job(id: jobId))
        jobLikeRepository.existsById({ JobLikeId id ->
            id.candidateId == candidateId && id.jobId == jobId
        }) >> true

        when:
        service.save(candidateId, jobId)

        then:
        thrown(IllegalArgumentException)
        0 * jobLikeRepository.save(_)
    }

    void "delete removes only authenticated candidate job like"() {
        given:
        UUID candidateId = UUID.randomUUID()
        UUID jobId = UUID.randomUUID()
        jobLikeRepository.existsById({ JobLikeId id ->
            id.candidateId == candidateId && id.jobId == jobId
        }) >> true

        when:
        service.delete(candidateId, jobId)

        then:
        1 * jobLikeRepository.deleteById({
            JobLikeId id -> id.candidateId == candidateId &&
                    id.jobId == jobId
        })
    }
}
