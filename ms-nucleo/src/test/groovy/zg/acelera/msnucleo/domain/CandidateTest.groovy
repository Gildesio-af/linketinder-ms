package zg.acelera.msnucleo.domain

import spock.lang.Specification
import zg.acelera.msnucleo.dto.address.AddressResponseDTO
import zg.acelera.msnucleo.dto.candidate.CandidateMatchResponseDTO
import zg.acelera.msnucleo.dto.candidate.CandidateResponseDTO

import java.time.LocalDate

class CandidateTest extends Specification {
    void "candidate response maps domain and addresses"() {
        given:
        Candidate candidate = candidate()
        AddressResponseDTO address = new AddressResponseDTO(city: 'São Paulo')

        when:
        CandidateResponseDTO result = CandidateResponseDTO.fromDomain(candidate, [address])

        then:
        result.id == candidate.userId
        result.name == 'Ana'
        result.email == 'ana@example.com'
        result.description == 'Developer'
        result.cpf == '12345678900'
        result.lastName == 'Silva'
        result.birthDate == LocalDate.of(1990, 5, 10)
        result.addresses == [address]
    }

    void "matched candidate response excludes cpf and addresses"() {
        given:
        Candidate candidate = candidate()

        when:
        CandidateMatchResponseDTO result = CandidateMatchResponseDTO.fromDomain(candidate)

        then:
        result.id == candidate.userId
        result.name == 'Ana'
        result.email == 'ana@example.com'
        result.description == 'Developer'
        result.lastName == 'Silva'
        result.birthDate == LocalDate.of(1990, 5, 10)
    }

    private static Candidate candidate() {
        UUID id = UUID.randomUUID()
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
}
