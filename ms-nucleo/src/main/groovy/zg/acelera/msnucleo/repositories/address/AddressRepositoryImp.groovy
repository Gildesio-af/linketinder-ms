package zg.acelera.msnucleo.repositories.address

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import zg.acelera.msnucleo.domain.Address

@Repository
class AddressRepositoryImp implements AddressRepository {
    private final AddressRepositoryJpa addressRepositoryJpa

    AddressRepositoryImp(AddressRepositoryJpa addressRepositoryJpa) {
        this.addressRepositoryJpa = addressRepositoryJpa
    }

    @Override Optional<Address> findById(UUID id) { addressRepositoryJpa.findById(id) }
    @Override Page<Address> findAll(Pageable pageable) { addressRepositoryJpa.findAll(pageable) }
    @Override List<Address> findByUserId(UUID userId) { addressRepositoryJpa.findAllByUser_Id(userId) }
    @Override Address save(Address address) { addressRepositoryJpa.save(address) }
    @Override boolean existsById(UUID id) { addressRepositoryJpa.existsById(id) }
    @Override void delete(UUID id) { addressRepositoryJpa.deleteById(id) }
}
