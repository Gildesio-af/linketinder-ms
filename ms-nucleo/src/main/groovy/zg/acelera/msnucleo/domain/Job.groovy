package zg.acelera.msnucleo.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "jobs")
class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id

    @Column(nullable = false, length = 100)
    String name

    @Column(nullable = false, columnDefinition = "TEXT")
    String description

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id", nullable = false)
    Address address

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "publisher_id", nullable = false)
    Company publisher
}
