package zg.acelera.msnucleo.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import spock.lang.Specification
import zg.acelera.msnucleo.domain.Address
import zg.acelera.msnucleo.domain.Company
import zg.acelera.msnucleo.domain.Job
import zg.acelera.msnucleo.domain.User
import zg.acelera.msnucleo.dto.job.JobCreateDTO
import zg.acelera.msnucleo.dto.job.JobResponseDTO
import zg.acelera.msnucleo.dto.job.JobUpdateDTO
import zg.acelera.msnucleo.repositories.address.AddressRepository
import zg.acelera.msnucleo.repositories.company.CompanyRepository
import zg.acelera.msnucleo.repositories.job.JobRepository
import zg.acelera.msnucleo.utils.exception.EntityNotFoundException

class JobServiceTest extends Specification {
    JobRepository jobRepository
    AddressRepository addressRepository
    CompanyRepository companyRepository
    JobService service

    def setup() {
        jobRepository = Mock()
        addressRepository = Mock()
        companyRepository = Mock()
        service = new JobService(jobRepository, addressRepository, companyRepository)
    }

    void "findById returns job"() {
        given:
        UUID id = UUID.randomUUID()
        Job job = buildJob(id)
        jobRepository.findById(id) >> Optional.of(job)

        when:
        JobResponseDTO result = service.findById(id)

        then:
        result.id == id
        result.name == 'Java Developer'
        result.description == 'Backend development'
    }

    void "findById throws when job does not exist"() {
        given:
        UUID id = UUID.randomUUID()
        jobRepository.findById(id) >> Optional.empty()

        when:
        service.findById(id)

        then:
        thrown(EntityNotFoundException)
    }

    void "findAll maps jobs"() {
        given:
        PageRequest pageable = PageRequest.of(0, 10)
        Job job = buildJob(UUID.randomUUID())
        jobRepository.findAll(pageable) >> new PageImpl([job], pageable, 1)

        when:
        Page<JobResponseDTO> result = service.findAll(pageable)

        then:
        result.totalElements == 1
        result.content[0].name == 'Java Developer'
    }

    void "findByPublisherId validates publisher and maps jobs"() {
        given:
        UUID publisherId = UUID.randomUUID()
        PageRequest pageable = PageRequest.of(0, 10)
        Company publisher = buildPublisher(publisherId)
        Job job = buildJob(UUID.randomUUID(), publisher)
        companyRepository.findById(publisherId) >> Optional.of(publisher)
        jobRepository.findByPublisherId(publisherId, pageable) >>
                new PageImpl([job], pageable, 1)

        when:
        Page<JobResponseDTO> result = service.findByPublisherId(publisherId, pageable)

        then:
        result.totalElements == 1
        result.content[0].publisherId == publisherId
    }

    void "findByPublisherId throws when publisher does not exist"() {
        given:
        UUID publisherId = UUID.randomUUID()
        companyRepository.findById(publisherId) >> Optional.empty()

        when:
        service.findByPublisherId(publisherId, PageRequest.of(0, 10))

        then:
        thrown(EntityNotFoundException)
        0 * jobRepository.findByPublisherId(_, _)
    }

    void "save resolves address and publisher and trims values"() {
        given:
        UUID addressId = UUID.randomUUID()
        UUID publisherId = UUID.randomUUID()
        UUID jobId = UUID.randomUUID()
        Address address = buildAddress(addressId)
        Company publisher = buildPublisher(publisherId)
        addressRepository.findById(addressId) >> Optional.of(address)
        companyRepository.findById(publisherId) >> Optional.of(publisher)
        JobCreateDTO dto = new JobCreateDTO(
                name: ' Java Developer ',
                description: ' Backend development ',
                addressId: addressId,
                publisherId: publisherId)

        when:
        JobResponseDTO result = service.save(dto)

        then:
        result.name == 'Java Developer'
        result.description == 'Backend development'
        result.addressId == addressId
        result.publisherId == publisherId
        1 * jobRepository.save({
            Job value -> value.name == 'Java Developer' &&
                    value.description == 'Backend development' &&
                    value.address == address &&
                    value.publisher == publisher
        }) >> { Job value ->
            value.id = jobId
            value
        }
    }

    void "save throws when address does not exist"() {
        given:
        UUID addressId = UUID.randomUUID()
        UUID publisherId = UUID.randomUUID()
        addressRepository.findById(addressId) >> Optional.empty()

        when:
        service.save(new JobCreateDTO(
                name: 'Java Developer',
                description: 'Backend development',
                addressId: addressId,
                publisherId: publisherId))

        then:
        thrown(EntityNotFoundException)
        0 * jobRepository.save(_)
    }

    void "save throws when publisher does not exist"() {
        given:
        UUID addressId = UUID.randomUUID()
        UUID publisherId = UUID.randomUUID()
        addressRepository.findById(addressId) >>
                Optional.of(buildAddress(addressId))
        companyRepository.findById(publisherId) >> Optional.empty()

        when:
        service.save(new JobCreateDTO(
                name: 'Java Developer',
                description: 'Backend development',
                addressId: addressId,
                publisherId: publisherId))

        then:
        thrown(EntityNotFoundException)
        0 * jobRepository.save(_)
    }

    void "update changes provided fields"() {
        given:
        UUID id = UUID.randomUUID()
        UUID addressId = UUID.randomUUID()
        Job job = buildJob(id)
        Address address = buildAddress(addressId)
        jobRepository.findById(id) >> Optional.of(job)
        addressRepository.findById(addressId) >> Optional.of(address)

        when:
        JobResponseDTO result = service.update(id, new JobUpdateDTO(
                name: ' Senior Java Developer ',
                description: ' Microservices development ',
                addressId: addressId))

        then:
        result.name == 'Senior Java Developer'
        result.description == 'Microservices development'
        result.addressId == addressId
        1 * jobRepository.save(job) >> job
    }

    void "update keeps unprovided fields"() {
        given:
        UUID id = UUID.randomUUID()
        Job job = buildJob(id)
        jobRepository.findById(id) >> Optional.of(job)

        when:
        JobResponseDTO result = service.update(
                id, new JobUpdateDTO(description: ' Updated description '))

        then:
        result.description == 'Updated description'
        result.name == 'Java Developer'
        result.addressId == job.address.id
        1 * jobRepository.save(job) >> job
        0 * addressRepository.findById(_)
    }

    void "update throws when new address does not exist"() {
        given:
        UUID id = UUID.randomUUID()
        UUID addressId = UUID.randomUUID()
        jobRepository.findById(id) >> Optional.of(buildJob(id))
        addressRepository.findById(addressId) >> Optional.empty()

        when:
        service.update(id, new JobUpdateDTO(addressId: addressId))

        then:
        thrown(EntityNotFoundException)
        0 * jobRepository.save(_)
    }

    void "delete removes existing job"() {
        given:
        UUID id = UUID.randomUUID()
        jobRepository.existsById(id) >> true

        when:
        service.delete(id)

        then:
        1 * jobRepository.delete(id)
    }

    void "delete throws when job does not exist"() {
        given:
        UUID id = UUID.randomUUID()
        jobRepository.existsById(id) >> false

        when:
        service.delete(id)

        then:
        thrown(EntityNotFoundException)
        0 * jobRepository.delete(_)
    }

    private static Job buildJob(UUID id) {
        buildJob(id, buildPublisher(UUID.randomUUID()))
    }

    private static Job buildJob(UUID id, Company publisher) {
        new Job(
                id: id,
                name: 'Java Developer',
                description: 'Backend development',
                address: buildAddress(UUID.randomUUID()),
                publisher: publisher)
    }

    private static Company buildPublisher(UUID id) {
        new Company(
                userId: id,
                user: new User(id: id, name: 'Acme'),
                cnpj: '12.345.678/0001-90')
    }

    private static Address buildAddress(UUID id) {
        new Address(
                id: id,
                cep: '01000-000',
                city: 'São Paulo',
                state: 'SP')
    }
}
