package zg.acelera.msnucleo.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import zg.acelera.msnucleo.domain.Company
import zg.acelera.msnucleo.domain.User
import zg.acelera.msnucleo.domain.UserRole
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
            toResponse(it)
        }
    }

    @Transactional
    CompanyResponseDTO save(CompanyCreateDTO dto) {
        User user = userService.create(dto.user, UserRole.COMPANY)
        Company company = createCompany(user, dto)
        toResponse(companyRepository.save(company))
    }

    @Transactional
    CompanyResponseDTO update(UUID id, CompanyUpdateDTO dto) {
        Company company = findEntity(id)
        updateUser(company.user, dto, id)
        updateCompany(company, dto, id)
        toResponse(companyRepository.save(company))
    }

    @Transactional
    void delete(UUID id) {
        ensureCompanyExists(id)
        ensureUserExists(id)
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

    private CompanyResponseDTO toResponse(Company company) {
        CompanyResponseDTO.fromDomain(company, findAddresses(company.userId))
    }

    private Company createCompany(User user, CompanyCreateDTO dto) {
        String cnpj = dto.company.cnpj.trim()
        validateCnpj(cnpj, null)
        new Company(user: user, cnpj: cnpj)
    }

    private void updateUser(User user, CompanyUpdateDTO dto, UUID id) {
        if (dto.name != null)
            user.name = dto.name.trim()
        if (dto.email != null)
            user.email = normalizeAndValidateEmail(dto.email, id)
        if (dto.description != null)
            user.description = dto.description.trim()
        if (hasUserChanges(dto))
            userRepository.save(user)
    }

    private String normalizeAndValidateEmail(String rawEmail, UUID id) {
        String email = rawEmail.trim().toLowerCase()
        userRepository.findByEmailIgnoreCase(email).ifPresent { existing ->
            if (existing.id != id)
                throw new IllegalArgumentException("User with email '$email' already exists")
        }
        email
    }

    private static boolean hasUserChanges(CompanyUpdateDTO dto) {
        dto.name != null || dto.email != null || dto.description != null
    }

    private void updateCompany(Company company, CompanyUpdateDTO dto, UUID id) {
        if (dto.cnpj != null) {
            String cnpj = dto.cnpj.trim()
            validateCnpj(cnpj, id)
            company.cnpj = cnpj
        }
    }

    private void validateCnpj(String cnpj, UUID ignoredId) {
        companyRepository.findByCnpj(cnpj).ifPresent { existing ->
            if (existing.userId != ignoredId)
                throw new IllegalArgumentException("Company with CNPJ '$cnpj' already exists")
        }
    }

    private void ensureCompanyExists(UUID id) {
        if (!companyRepository.existsById(id))
            throw new EntityNotFoundException("Company not found with id: $id")
    }

    private void ensureUserExists(UUID id) {
        if (!userRepository.existsById(id))
            throw new EntityNotFoundException("User not found with id: $id")
    }
}
