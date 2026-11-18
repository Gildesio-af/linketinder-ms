package zg.acelera.msnucleo.repositories.address

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import zg.acelera.msnucleo.domain.Address

interface AddressRepository {
    Optional<Address> findById(UUID id)
    Page<Address> findAll(Pageable pageable)
    List<Address> findByUserId(UUID userId)
    Address save(Address address)
    boolean existsById(UUID id)
    void delete(UUID id)
}
