package zg.acelera.msnucleo.domain

import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.MapsId
import jakarta.persistence.Table

@Entity
@Table(name = "companies_like")
class CompanyLike {
    @EmbeddedId
    CompanyLikeId id

    @ManyToOne
    @MapsId("companyId")
    @JoinColumn(name = "company_id", nullable = false)
    Company company

    @ManyToOne
    @MapsId("likedCandidateId")
    @JoinColumn(name = "liked_candidate_id", nullable = false)
    Candidate candidate
}
