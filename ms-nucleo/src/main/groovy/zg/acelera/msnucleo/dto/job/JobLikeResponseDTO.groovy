package zg.acelera.msnucleo.dto.job

import zg.acelera.msnucleo.domain.JobLike

class JobLikeResponseDTO {
    UUID candidateId
    UUID jobId

    static JobLikeResponseDTO fromDomain(JobLike jobLike) {
        new JobLikeResponseDTO(
                candidateId: jobLike.id.candidateId,
                jobId: jobLike.id.jobId)
    }
}
