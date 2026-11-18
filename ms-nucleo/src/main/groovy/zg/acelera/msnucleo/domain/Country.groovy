package zg.acelera.msnucleo.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "countries")
class Country {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id

    @Column(nullable = false, length = 75)
    String name

    @Column(nullable = false, unique = true, length = 2, columnDefinition = "CHAR(2)")
    String code
}
