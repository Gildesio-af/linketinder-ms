package zg.acelera.msnucleo.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint

import java.time.LocalDateTime

@Entity
@Table(name = "matches", uniqueConstraints = [
        @UniqueConstraint(name = "unique_match_candidate_job",
                columnNames = ["candidate_id", "job_id"])
])
class Match {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id

    @ManyToOne
    @JoinColumn(name = "candidate_id", nullable = false)
    Candidate candidate

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    Company company

    @ManyToOne
    @JoinColumn(name = "job_id", nullable = false)
    Job job

    @Column(name = "created_at")
    LocalDateTime createdAt
}
