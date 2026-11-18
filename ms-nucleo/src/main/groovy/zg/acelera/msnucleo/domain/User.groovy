package zg.acelera.msnucleo.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "users")
class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id

    @Column(nullable = false, length = 100)
    String name

    @Column(nullable = false, unique = true, length = 100)
    String email

    @Column(nullable = false, length = 100)
    String password

    @Column(nullable = false, columnDefinition = "TEXT")
    String description

}
