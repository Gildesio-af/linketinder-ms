package zg.acelera.msnucleo.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import zg.acelera.msnucleo.domain.Candidate
import zg.acelera.msnucleo.domain.User
import zg.acelera.msnucleo.domain.UserRole
import zg.acelera.msnucleo.dto.address.AddressResponseDTO
import zg.acelera.msnucleo.dto.candidate.CandidateCreateDTO
import zg.acelera.msnucleo.dto.candidate.CandidateMatchResponseDTO
import zg.acelera.msnucleo.dto.candidate.CandidateResponseDTO
import zg.acelera.msnucleo.dto.candidate.CandidateUpdateDTO
import zg.acelera.msnucleo.dto.ms_skill.SkillResponseDTO
import zg.acelera.msnucleo.infrastructure.client.SkillClient
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
    private final SkillClient skillClient

    CandidateService(CandidateRepository candidateRepository,
                     UserRepository userRepository,
                     AddressRepository addressRepository,
                     UserService userService,
                     SkillClient skillClient) {
        this.candidateRepository = candidateRepository
        this.userRepository = userRepository
        this.addressRepository = addressRepository
        this.userService = userService
        this.skillClient = skillClient
    }

    @Transactional(readOnly = true)
    CandidateResponseDTO findById(UUID id) {
        Candidate candidate = findEntity(id)
        CandidateResponseDTO.fromDomain(candidate, findAddresses(id), findSkills(id))
    }

    @Transactional(readOnly = true)
    CandidateMatchResponseDTO findMatchedCandidate(UUID id) {
        Candidate candidate = findEntity(id)
        CandidateMatchResponseDTO.fromDomain(candidate, findSkills(id))
    }

    @Transactional(readOnly = true)
    Page<CandidateResponseDTO> findAll(Pageable pageable) {
        Page<Candidate> page = candidateRepository.findAll(pageable)
        if (page.isEmpty())
            return page.map { toResponse(it, []) }

        List<UUID> userIds = page.content.collect { it.userId }.unique()
        Map<UUID, List<SkillResponseDTO>> skillsByUser = [:]
        userIds.collate(100).each { batch ->
            skillsByUser.putAll(skillClient.getSkillsByUserIds(batch))
        }
        page.map { candidate ->
            toResponse(candidate, skillsByUser.getOrDefault(candidate.userId, []))
        }
    }

    @Transactional
    CandidateResponseDTO save(CandidateCreateDTO dto) {
        User user = userService.create(dto.user, UserRole.CANDIDATE)
        Candidate candidate = createCandidate(user, dto)
        Candidate savedCandidate = candidateRepository.save(candidate)
        CandidateResponseDTO.fromDomain(savedCandidate, findAddresses(savedCandidate.userId), [])
    }

    @Transactional
    CandidateResponseDTO update(UUID id, CandidateUpdateDTO dto) {
        Candidate candidate = findEntity(id)
        updateUser(candidate.user, dto, id)
        updateCandidate(candidate, dto, id)
        Candidate savedCandidate = candidateRepository.save(candidate)
        CandidateResponseDTO.fromDomain(savedCandidate, findAddresses(id), findSkills(id))
    }

    @Transactional
    void delete(UUID id) {
        ensureCandidateExists(id)
        ensureUserExists(id)
        candidateRepository.delete(id)
        userRepository.delete(id)
    }

    private Candidate findEntity(UUID id) {
        candidateRepository.findById(id)
                .orElseThrow { new EntityNotFoundException("Candidate not found with id: $id") }
    }

    private List<AddressResponseDTO> findAddresses(UUID userId) {
        addressRepository.findByUserId(userId).collect { AddressResponseDTO.fromDomain(it) }
    }

    private List<SkillResponseDTO> findSkills(UUID userId) {
        if (skillClient == null)
            return []
        List<SkillResponseDTO> skills = skillClient.getSkillsByUserId(userId)
        skills ?: []
    }

    private CandidateResponseDTO toResponse(Candidate candidate, List<SkillResponseDTO> skills) {
        CandidateResponseDTO.fromDomain(candidate, findAddresses(candidate.userId), skills)
    }

    private Candidate createCandidate(User user, CandidateCreateDTO dto) {
        String cpf = dto.candidate.cpf.trim()
        validateCpf(cpf, null)
        new Candidate(
                user: user,
                cpf: cpf,
                lastName: dto.candidate.lastName.trim(),
                birthDate: dto.candidate.birthDate)
    }

    private void updateUser(User user, CandidateUpdateDTO dto, UUID id) {
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

    private static boolean hasUserChanges(CandidateUpdateDTO dto) {
        dto.name != null || dto.email != null || dto.description != null
    }

    private void updateCandidate(Candidate candidate, CandidateUpdateDTO dto, UUID id) {
        if (dto.cpf != null) {
            String cpf = dto.cpf.trim()
            validateCpf(cpf, id)
            candidate.cpf = cpf
        }
        if (dto.lastName != null)
            candidate.lastName = dto.lastName.trim()
        if (dto.birthDate != null)
            candidate.birthDate = dto.birthDate
    }

    private void validateCpf(String cpf, UUID ignoredId) {
        candidateRepository.findByCpf(cpf).ifPresent { existing ->
            if (existing.userId != ignoredId)
                throw new IllegalArgumentException("Candidate with CPF '$cpf' already exists")
        }
    }

    private void ensureCandidateExists(UUID id) {
        if (!candidateRepository.existsById(id))
            throw new EntityNotFoundException("Candidate not found with id: $id")
    }

    private void ensureUserExists(UUID id) {
        if (!userRepository.existsById(id))
            throw new EntityNotFoundException("User not found with id: $id")
    }
}
