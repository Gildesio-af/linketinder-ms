package zg.acelera.msnucleo.domain

import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.MapsId
import jakarta.persistence.Table

@Entity
@Table(name = "jobs_like")
class JobLike {
    @EmbeddedId
    JobLikeId id

    @ManyToOne
    @MapsId("candidateId")
    @JoinColumn(name = "candidate_id", nullable = false)
    Candidate candidate

    @ManyToOne
    @MapsId("jobId")
    @JoinColumn(name = "job_id", nullable = false)
    Job job
}
