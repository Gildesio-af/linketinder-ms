package zg.acelera.msnucleo.repositories.address

import org.springframework.data.jpa.repository.JpaRepository
import zg.acelera.msnucleo.domain.Address

interface AddressRepositoryJpa extends JpaRepository<Address, UUID> {
    List<Address> findAllByUser_Id(UUID userId)
}
