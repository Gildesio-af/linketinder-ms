package zg.acelera.msnucleo.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.MapsId
import jakarta.persistence.OneToOne
import jakarta.persistence.Table

import java.time.LocalDate

@Entity
@Table(name = "candidates")
class Candidate {
    @Id
    @Column(name = "user_id")
    UUID userId

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    User user

    @Column(nullable = false, unique = true, length = 14)
    String cpf

    @Column(nullable = false, length = 100)
    String lastName

    @Column(nullable = false)
    LocalDate birthDate
}
