package zg.acelera.msnucleo.domain

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

import java.io.Serializable
import java.util.Objects

@Embeddable
class JobLikeId implements Serializable {
    @Column(name = "candidate_id")
    UUID candidateId

    @Column(name = "job_id")
    UUID jobId

    boolean equals(Object other) {
        if (this.is(other)) return true
        if (!(other instanceof JobLikeId)) return false
        candidateId == other.candidateId && jobId == other.jobId
    }

    int hashCode() {
        Objects.hash(candidateId, jobId)
    }
}
