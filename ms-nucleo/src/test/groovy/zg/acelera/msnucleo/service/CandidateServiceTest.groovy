package zg.acelera.msnucleo.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import spock.lang.Specification
import zg.acelera.msnucleo.domain.Address
import zg.acelera.msnucleo.domain.Candidate
import zg.acelera.msnucleo.domain.Country
import zg.acelera.msnucleo.domain.User
import zg.acelera.msnucleo.dto.address.AddressResponseDTO
import zg.acelera.msnucleo.dto.candidate.CandidateCreateDTO
import zg.acelera.msnucleo.dto.candidate.CandidateDataCreateDTO
import zg.acelera.msnucleo.dto.candidate.CandidateMatchResponseDTO
import zg.acelera.msnucleo.dto.candidate.CandidateResponseDTO
import zg.acelera.msnucleo.dto.candidate.CandidateUpdateDTO
import zg.acelera.msnucleo.dto.user.UserCreateDTO
import zg.acelera.msnucleo.repositories.address.AddressRepository
import zg.acelera.msnucleo.repositories.candidate.CandidateRepository
import zg.acelera.msnucleo.repositories.user.UserRepository
import zg.acelera.msnucleo.utils.exception.EntityNotFoundException

import java.time.LocalDate

class CandidateServiceTest extends Specification {
    CandidateRepository candidateRepository
    UserRepository userRepository
    AddressRepository addressRepository
    UserService userService
    CandidateService service

    def setup() {
        candidateRepository = Mock()
        userRepository = Mock()
        addressRepository = Mock()
        userService = Mock()
        service = new CandidateService(candidateRepository, userRepository,
                addressRepository, userService)
    }

    void "findById returns candidate with addresses"() {
        given:
        UUID id = UUID.randomUUID()
        Candidate candidate = buildCandidate(id)
        Address address = address(candidate.user)
        candidateRepository.findById(id) >> Optional.of(candidate)
        addressRepository.findByUserId(id) >> [address]

        when:
        CandidateResponseDTO result = service.findById(id)

        then:
        result.id == id
        result.name == 'Ana'
        result.addresses.size() == 1
    }

    void "findById throws when candidate does not exist"() {
        given:
        UUID id = UUID.randomUUID()
        candidateRepository.findById(id) >> Optional.empty()

        when:
        service.findById(id)

        then:
        thrown(EntityNotFoundException)
    }

    void "findMatchedCandidate returns match data"() {
        given:
        UUID id = UUID.randomUUID()
        candidateRepository.findById(id) >> Optional.of(buildCandidate(id))

        when:
        CandidateMatchResponseDTO result = service.findMatchedCandidate(id)

        then:
        result.id == id
        result.lastName == 'Silva'
    }

    void "findAll maps candidates"() {
        given:
        PageRequest pageable = PageRequest.of(0, 10)
        Candidate candidate = buildCandidate(UUID.randomUUID())
        candidateRepository.findAll(pageable) >> new PageImpl([candidate], pageable, 1)
        addressRepository.findByUserId(candidate.userId) >> []

        when:
        Page<CandidateResponseDTO> result = service.findAll(pageable)

        then:
        result.totalElements == 1
        result.content[0].cpf == '12345678900'
    }

    void "save creates user and candidate"() {
        given:
        UUID id = UUID.randomUUID()
        User user = new User(id: id, name: 'Ana', email: 'ana@example.com',
                password: 'secret1', description: 'Developer')
        CandidateCreateDTO dto = createDto()
        userService.create(dto.user) >> user
        candidateRepository.findByCpf('12345678900') >> Optional.empty()
        addressRepository.findByUserId(id) >> []

        when:
        CandidateResponseDTO result = service.save(dto)

        then:
        result.name == 'Ana'
        result.cpf == '12345678900'
        1 * candidateRepository.save({
            Candidate value -> value.user == user &&
                    value.cpf == '12345678900' &&
                    value.lastName == 'Silva'
        }) >> { Candidate value ->
            value.userId = id
            value
        }
    }

    void "save rejects duplicate cpf"() {
        given:
        User user = new User(id: UUID.randomUUID(), name: 'Ana')
        CandidateCreateDTO dto = createDto()
        userService.create(dto.user) >> user
        candidateRepository.findByCpf('12345678900') >>
                Optional.of(buildCandidate(UUID.randomUUID()))

        when:
        service.save(dto)

        then:
        thrown(IllegalArgumentException)
        0 * candidateRepository.save(_)
    }

    void "update changes provided candidate and user fields"() {
        given:
        UUID id = UUID.randomUUID()
        Candidate candidate = buildCandidate(id)
        CandidateUpdateDTO dto = new CandidateUpdateDTO(
                name: ' Ana Maria ',
                email: ' ANA.NEW@EXAMPLE.COM ',
                description: ' Senior Developer ',
                cpf: ' 98765432100 ',
                lastName: ' Santos ',
                birthDate: LocalDate.of(1992, 3, 20))
        candidateRepository.findById(id) >> Optional.of(candidate)
        userRepository.findByEmailIgnoreCase('ana.new@example.com') >> Optional.empty()
        candidateRepository.findByCpf('98765432100') >> Optional.empty()
        addressRepository.findByUserId(id) >> []

        when:
        CandidateResponseDTO result = service.update(id, dto)

        then:
        result.name == 'Ana Maria'
        result.email == 'ana.new@example.com'
        result.description == 'Senior Developer'
        result.cpf == '98765432100'
        result.lastName == 'Santos'
        result.birthDate == LocalDate.of(1992, 3, 20)
        1 * userRepository.save(candidate.user) >> candidate.user
        1 * candidateRepository.save(candidate) >> candidate
    }

    void "update rejects duplicate email"() {
        given:
        UUID id = UUID.randomUUID()
        Candidate candidate = buildCandidate(id)
        User existing = new User(id: UUID.randomUUID(), email: 'used@example.com')
        candidateRepository.findById(id) >> Optional.of(candidate)
        userRepository.findByEmailIgnoreCase('used@example.com') >> Optional.of(existing)

        when:
        service.update(id, new CandidateUpdateDTO(email: ' used@example.com '))

        then:
        thrown(IllegalArgumentException)
        0 * userRepository.save(_)
        0 * candidateRepository.save(_)
    }

    void "update allows email from same candidate"() {
        given:
        UUID id = UUID.randomUUID()
        Candidate candidate = buildCandidate(id)
        candidateRepository.findById(id) >> Optional.of(candidate)
        userRepository.findByEmailIgnoreCase('ana@example.com') >>
                Optional.of(candidate.user)
        addressRepository.findByUserId(id) >> []

        when:
        CandidateResponseDTO result = service.update(
                id, new CandidateUpdateDTO(email: ' ana@example.com '))

        then:
        result.email == 'ana@example.com'
        1 * userRepository.save(candidate.user) >> candidate.user
        1 * candidateRepository.save(candidate) >> candidate
    }

    void "update rejects duplicate cpf"() {
        given:
        UUID id = UUID.randomUUID()
        Candidate candidate = buildCandidate(id)
        Candidate existing = buildCandidate(UUID.randomUUID())
        candidateRepository.findById(id) >> Optional.of(candidate)
        candidateRepository.findByCpf('98765432100') >> Optional.of(existing)

        when:
        service.update(id, new CandidateUpdateDTO(cpf: '98765432100'))

        then:
        thrown(IllegalArgumentException)
        0 * candidateRepository.save(_)
    }

    void "delete removes existing candidate and user"() {
        given:
        UUID id = UUID.randomUUID()
        candidateRepository.existsById(id) >> true
        userRepository.existsById(id) >> true

        when:
        service.delete(id)

        then:
        1 * candidateRepository.delete(id)
        1 * userRepository.delete(id)
    }

    void "delete throws when candidate does not exist"() {
        given:
        UUID id = UUID.randomUUID()
        candidateRepository.existsById(id) >> false

        when:
        service.delete(id)

        then:
        thrown(EntityNotFoundException)
        0 * candidateRepository.delete(_)
        0 * userRepository.delete(_)
    }

    void "delete throws when user does not exist"() {
        given:
        UUID id = UUID.randomUUID()
        candidateRepository.existsById(id) >> true
        userRepository.existsById(id) >> false

        when:
        service.delete(id)

        then:
        thrown(EntityNotFoundException)
        0 * candidateRepository.delete(_)
        0 * userRepository.delete(_)
    }

    private static CandidateCreateDTO createDto() {
        new CandidateCreateDTO(
                user: new UserCreateDTO(
                        name: 'Ana',
                        email: 'ana@example.com',
                        password: 'secret1',
                        description: 'Developer'),
                candidate: new CandidateDataCreateDTO(
                        cpf: ' 12345678900 ',
                        lastName: ' Silva ',
                        birthDate: LocalDate.of(1990, 5, 10)))
    }

    private static Candidate buildCandidate(UUID id) {
        User user = new User(
                id: id,
                name: 'Ana',
                email: 'ana@example.com',
                description: 'Developer')
        new Candidate(
                userId: id,
                user: user,
                cpf: '12345678900',
                lastName: 'Silva',
                birthDate: LocalDate.of(1990, 5, 10))
    }

    private static Address address(User user) {
        new Address(
                user: user,
                cep: '01000-000',
                city: 'São Paulo',
                state: 'SP',
                country: new Country(id: UUID.randomUUID(), name: 'Brazil', code: 'BR'))
    }
}
