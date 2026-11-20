package zg.acelera.msnucleo.service

import spock.lang.Specification
import zg.acelera.msnucleo.domain.Candidate
import zg.acelera.msnucleo.domain.Company
import zg.acelera.msnucleo.domain.Job
import zg.acelera.msnucleo.domain.JobLike
import zg.acelera.msnucleo.domain.Match
import zg.acelera.msnucleo.repositories.company.CompanyLikeRepository
import zg.acelera.msnucleo.repositories.job.JobLikeRepository
import zg.acelera.msnucleo.repositories.match.MatchRepository

class MatchServiceTest extends Specification {
    MatchRepository matchRepository
    JobLikeRepository jobLikeRepository
    CompanyLikeRepository companyLikeRepository
    MatchService service

    def setup() {
        matchRepository = Mock()
        jobLikeRepository = Mock()
        companyLikeRepository = Mock()
        service = new MatchService(
                matchRepository,
                jobLikeRepository,
                companyLikeRepository)
    }

    void "company like creates match for liked candidate job"() {
        given:
        UUID candidateId = UUID.randomUUID()
        UUID companyId = UUID.randomUUID()
        UUID jobId = UUID.randomUUID()
        Candidate candidate = new Candidate(userId: candidateId)
        Company company = new Company(userId: companyId)
        Job job = new Job(id: jobId, publisher: company)
        jobLikeRepository.findAllByCandidateId(candidateId) >>
                [new JobLike(job: job)]
        matchRepository.findByCandidateIdAndJobId(candidateId, jobId) >>
                Optional.empty()

        when:
        service.createMatchesForCompanyLike(company, candidate)

        then:
        1 * matchRepository.save({
            Match match ->
                    match.candidate == candidate &&
                    match.company == company &&
                    match.job == job &&
                    match.createdAt != null
        }) >> { Match match -> match }
    }

    void "job like creates match when company already likes candidate"() {
        given:
        UUID candidateId = UUID.randomUUID()
        UUID companyId = UUID.randomUUID()
        UUID jobId = UUID.randomUUID()
        Candidate candidate = new Candidate(userId: candidateId)
        Company company = new Company(userId: companyId)
        Job job = new Job(id: jobId, publisher: company)
        companyLikeRepository.existsByCompanyIdAndCandidateId(companyId, candidateId) >> true
        matchRepository.findByCandidateIdAndJobId(candidateId, jobId) >>
                Optional.empty()

        when:
        service.createMatchForJobLike(candidate, job)

        then:
        1 * matchRepository.save({
                Match match ->
                    match.candidate == candidate &&
                    match.company == company &&
                    match.job == job
        }) >> { Match match -> match }
    }
}
