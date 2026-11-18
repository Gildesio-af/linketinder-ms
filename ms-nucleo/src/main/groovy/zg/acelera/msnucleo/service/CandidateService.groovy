package zg.acelera.msnucleo.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import zg.acelera.msnucleo.domain.Candidate
import zg.acelera.msnucleo.domain.User
import zg.acelera.msnucleo.dto.address.AddressResponseDTO
import zg.acelera.msnucleo.dto.candidate.CandidateCreateDTO
import zg.acelera.msnucleo.dto.candidate.CandidateMatchResponseDTO
import zg.acelera.msnucleo.dto.candidate.CandidateResponseDTO
import zg.acelera.msnucleo.dto.candidate.CandidateUpdateDTO
import zg.acelera.msnucleo.repositories.address.AddressRepository
import zg.acelera.msnucleo.repositories.candidate.CandidateRepository
import zg.acelera.msnucleo.repositories.user.UserRepository
import zg.acelera.msnucleo.utils.exception.EntityNotFoundException

@Service
class CandidateService {
    private final CandidateRepository candidateRepository
    private final UserRepository userRepository
    private final AddressRepository addressRepository
    private final UserService userService

    CandidateService(CandidateRepository candidateRepository,
                     UserRepository userRepository,
                     AddressRepository addressRepository,
                     UserService userService) {
        this.candidateRepository = candidateRepository
        this.userRepository = userRepository
        this.addressRepository = addressRepository
        this.userService = userService
    }

    @Transactional(readOnly = true)
    CandidateResponseDTO findById(UUID id) {
        Candidate candidate = findEntity(id)
        CandidateResponseDTO.fromDomain(candidate, findAddresses(id))
    }

    @Transactional(readOnly = true)
    CandidateMatchResponseDTO findMatchedCandidate(UUID id) {
        CandidateMatchResponseDTO.fromDomain(findEntity(id))
    }

    @Transactional(readOnly = true)
    Page<CandidateResponseDTO> findAll(Pageable pageable) {
        candidateRepository.findAll(pageable).map {
            CandidateResponseDTO.fromDomain(it, findAddresses(it.userId))
        }
    }

    @Transactional
    CandidateResponseDTO save(CandidateCreateDTO dto) {
        User user = userService.create(dto.user)
        validateCpf(dto.candidate.cpf.trim(), null)

        Candidate candidate = new Candidate(
                user: user,
                cpf: dto.candidate.cpf.trim(),
                lastName: dto.candidate.lastName.trim(),
                birthDate: dto.candidate.birthDate)
        CandidateResponseDTO.fromDomain(
                candidateRepository.save(candidate),
                findAddresses(user.id))
    }

    @Transactional
    CandidateResponseDTO update(UUID id, CandidateUpdateDTO dto) {
        Candidate candidate = findEntity(id)

        if (dto.name != null)
            candidate.user.name = dto.name.trim()
        if (dto.email != null) {
            String email = dto.email.trim().toLowerCase()
            if (userRepository.findByEmailIgnoreCase(email).isPresent()) {
                User existing = userRepository.findByEmailIgnoreCase(email).get()
                if (existing.id != id)
                    throw new IllegalArgumentException("User with email '$email' already exists")
            }
            candidate.user.email = email
        }
        if (dto.description != null)
            candidate.user.description = dto.description.trim()
        if (dto.name != null || dto.email != null || dto.description != null)
            userRepository.save(candidate.user)

        if (dto.cpf != null) {
            String cpf = dto.cpf.trim()
            validateCpf(cpf, id)
            candidate.cpf = cpf
        }
        if (dto.lastName != null)
            candidate.lastName = dto.lastName.trim()
        if (dto.birthDate != null)
            candidate.birthDate = dto.birthDate

        CandidateResponseDTO.fromDomain(
                candidateRepository.save(candidate),
                findAddresses(id))
    }

    @Transactional
    void delete(UUID id) {
        if (!candidateRepository.existsById(id))
            throw new EntityNotFoundException("Candidate not found with id: $id")
        if (!userRepository.existsById(id))
            throw new EntityNotFoundException("User not found with id: $id")

        candidateRepository.delete(id)
        userRepository.delete(id)
    }

    private Candidate findEntity(UUID id) {
        candidateRepository.findById(id)
                .orElseThrow { new EntityNotFoundException("Candidate not found with id: $id") }
    }

    private User findUser(UUID id) {
        userRepository.findById(id)
                .orElseThrow { new EntityNotFoundException("User not found with id: $id") }
    }

    private List<AddressResponseDTO> findAddresses(UUID userId) {
        addressRepository.findByUserId(userId).collect { AddressResponseDTO.fromDomain(it) }
    }

    private void validateCpf(String cpf, UUID ignoredId) {
        if (candidateRepository.findByCpf(cpf).isPresent()) {
            Candidate existing = candidateRepository.findByCpf(cpf).get()
            if (existing.userId != ignoredId)
                throw new IllegalArgumentException("Candidate with CPF '$cpf' already exists")
        }
    }
}
