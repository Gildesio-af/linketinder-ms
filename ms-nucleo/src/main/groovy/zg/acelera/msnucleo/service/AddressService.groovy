package zg.acelera.msnucleo.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import zg.acelera.msnucleo.domain.Address
import zg.acelera.msnucleo.domain.Country
import zg.acelera.msnucleo.domain.User
import zg.acelera.msnucleo.dto.address.AddressCreateDTO
import zg.acelera.msnucleo.dto.address.AddressResponseDTO
import zg.acelera.msnucleo.dto.address.AddressUpdateDTO
import zg.acelera.msnucleo.repositories.address.AddressRepository
import zg.acelera.msnucleo.repositories.country.CountryRepository
import zg.acelera.msnucleo.repositories.user.UserRepository
import zg.acelera.msnucleo.utils.exception.EntityNotFoundException

@Service
class AddressService {
    private final AddressRepository addressRepository
    private final CountryRepository countryRepository
    private final UserRepository userRepository

    AddressService(AddressRepository addressRepository,
                   CountryRepository countryRepository,
                   UserRepository userRepository) {
        this.addressRepository = addressRepository
        this.countryRepository = countryRepository
        this.userRepository = userRepository
    }

    @Transactional(readOnly = true)
    AddressResponseDTO findById(UUID id) {
        AddressResponseDTO.fromDomain(findEntity(id))
    }

    @Transactional(readOnly = true)
    Page<AddressResponseDTO> findAll(Pageable pageable) {
        addressRepository.findAll(pageable).map { AddressResponseDTO.fromDomain(it) }
    }

    @Transactional(readOnly = true)
    List<AddressResponseDTO> findByUserId(UUID userId) {
        addressRepository.findByUserId(userId).collect { AddressResponseDTO.fromDomain(it) }
    }

    @Transactional
    AddressResponseDTO save(AddressCreateDTO dto) {
        Country country = findCountry(dto.countryId)
        User user = findOptionalUser(dto.userId)

        Address address = new Address(
                user: user,
                cep: dto.cep?.trim(),
                street: dto.street?.trim(),
                number: dto.number?.trim(),
                complement: dto.complement?.trim(),
                neighborhood: dto.neighborhood?.trim(),
                city: dto.city.trim(),
                state: dto.state.trim(),
                country: country)
        AddressResponseDTO.fromDomain(addressRepository.save(address))
    }

    @Transactional
    AddressResponseDTO update(UUID id, AddressUpdateDTO dto) {
        Address address = findEntity(id)
        if (dto.userId != null)
            address.user = findOptionalUser(dto.userId)
        if (dto.countryId != null)
            address.country = findCountry(dto.countryId)
        if (dto.cep != null)
            address.cep = dto.cep.trim()
        if (dto.street != null)
            address.street = dto.street.trim()
        if (dto.number != null)
            address.number = dto.number.trim()
        if (dto.complement != null)
            address.complement = dto.complement.trim()
        if (dto.neighborhood != null)
            address.neighborhood = dto.neighborhood.trim()
        if (dto.city != null)
            address.city = dto.city.trim()
        if (dto.state != null)
            address.state = dto.state.trim()
        AddressResponseDTO.fromDomain(addressRepository.save(address))
    }

    @Transactional
    void delete(UUID id) {
        if (!addressRepository.existsById(id))
            throw new EntityNotFoundException("Address not found with id: $id")
        addressRepository.delete(id)
    }

    private Address findEntity(UUID id) {
        addressRepository.findById(id)
                .orElseThrow { new EntityNotFoundException("Address not found with id: $id") }
    }

    private Country findCountry(UUID id) {
        countryRepository.findById(id)
                .orElseThrow { new EntityNotFoundException("Country not found with id: $id") }
    }

    private User findOptionalUser(UUID id) {
        if (id == null)
            return null
        userRepository.findById(id)
                .orElseThrow { new EntityNotFoundException("User not found with id: $id") }
    }
}
