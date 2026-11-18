package zg.acelera.msnucleo.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.MapsId
import jakarta.persistence.OneToOne
import jakarta.persistence.Table

@Entity
@Table(name = "companies")
class Company {
    @Id
    @Column(name = "user_id")
    UUID userId

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    User user

    @Column(nullable = false, unique = true, length = 18)
    String cnpj
}
