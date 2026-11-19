package zg.acelera.msnucleo.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import zg.acelera.msnucleo.domain.Company
import zg.acelera.msnucleo.domain.User
import zg.acelera.msnucleo.dto.address.AddressResponseDTO
import zg.acelera.msnucleo.dto.company.CompanyCreateDTO
import zg.acelera.msnucleo.dto.company.CompanyResponseDTO
import zg.acelera.msnucleo.dto.company.CompanyUpdateDTO
import zg.acelera.msnucleo.repositories.address.AddressRepository
import zg.acelera.msnucleo.repositories.company.CompanyRepository
import zg.acelera.msnucleo.repositories.user.UserRepository
import zg.acelera.msnucleo.utils.exception.EntityNotFoundException

@Service
class CompanyService {
    private final CompanyRepository companyRepository
    private final UserRepository userRepository
    private final AddressRepository addressRepository
    private final UserService userService

    CompanyService(CompanyRepository companyRepository,
                   UserRepository userRepository,
                   AddressRepository addressRepository,
                   UserService userService) {
        this.companyRepository = companyRepository
        this.userRepository = userRepository
        this.addressRepository = addressRepository
        this.userService = userService
    }

    @Transactional(readOnly = true)
    CompanyResponseDTO findById(UUID id) {
        Company company = findEntity(id)
        CompanyResponseDTO.fromDomain(company, findAddresses(id))
    }

    @Transactional(readOnly = true)
    Page<CompanyResponseDTO> findAll(Pageable pageable) {
        companyRepository.findAll(pageable).map {
            CompanyResponseDTO.fromDomain(it, findAddresses(it.userId))
        }
    }

    @Transactional
    CompanyResponseDTO save(CompanyCreateDTO dto) {
        User user = userService.create(dto.user)
        validateCnpj(dto.company.cnpj.trim(), null)

        Company company = new Company(
                user: user,
                cnpj: dto.company.cnpj.trim())
        CompanyResponseDTO.fromDomain(
                companyRepository.save(company),
                findAddresses(user.id))
    }

    @Transactional
    CompanyResponseDTO update(UUID id, CompanyUpdateDTO dto) {
        Company company = findEntity(id)

        if (dto.name != null)
            company.user.name = dto.name.trim()
        if (dto.email != null) {
            String email = dto.email.trim().toLowerCase()
            if (userRepository.findByEmailIgnoreCase(email).isPresent()) {
                User existing = userRepository.findByEmailIgnoreCase(email).get()
                if (existing.id != id)
                    throw new IllegalArgumentException("User with email '$email' already exists")
            }
            company.user.email = email
        }
        if (dto.description != null)
            company.user.description = dto.description.trim()
        if (dto.name != null || dto.email != null || dto.description != null)
            userRepository.save(company.user)

        if (dto.cnpj != null) {
            String cnpj = dto.cnpj.trim()
            validateCnpj(cnpj, id)
            company.cnpj = cnpj
        }

        CompanyResponseDTO.fromDomain(
                companyRepository.save(company),
                findAddresses(id))
    }

    @Transactional
    void delete(UUID id) {
        if (!companyRepository.existsById(id))
            throw new EntityNotFoundException("Company not found with id: $id")
        if (!userRepository.existsById(id))
            throw new EntityNotFoundException("User not found with id: $id")

        companyRepository.delete(id)
        userRepository.delete(id)
    }

    private Company findEntity(UUID id) {
        companyRepository.findById(id)
                .orElseThrow { new EntityNotFoundException("Company not found with id: $id") }
    }

    private List<AddressResponseDTO> findAddresses(UUID userId) {
        addressRepository.findByUserId(userId).collect { AddressResponseDTO.fromDomain(it) }
    }

    private void validateCnpj(String cnpj, UUID ignoredId) {
        if (companyRepository.findByCnpj(cnpj).isPresent()) {
            Company existing = companyRepository.findByCnpj(cnpj).get()
            if (existing.userId != ignoredId)
                throw new IllegalArgumentException("Company with CNPJ '$cnpj' already exists")
        }
    }
}
