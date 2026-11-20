package zg.acelera.msnucleo.domain

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

import java.io.Serializable

@Embeddable
class CompanyLikeId implements Serializable {
    @Column(name = "company_id")
    UUID companyId

    @Column(name = "liked_candidate_id")
    UUID likedCandidateId

    boolean equals(Object other) {
        if (this.is(other)) return true
        if (!(other instanceof CompanyLikeId)) return false
        return companyId == other.companyId &&
                likedCandidateId == other.likedCandidateId
    }

    int hashCode() {
        Objects.hash(companyId, likedCandidateId)
    }
}
