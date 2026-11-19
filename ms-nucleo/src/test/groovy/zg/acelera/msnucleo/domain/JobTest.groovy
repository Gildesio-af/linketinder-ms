package zg.acelera.msnucleo.domain

import spock.lang.Specification
import zg.acelera.msnucleo.dto.job.JobResponseDTO

class JobTest extends Specification {
    void "job response maps domain relations"() {
        given:
        UUID jobId = UUID.randomUUID()
        UUID addressId = UUID.randomUUID()
        UUID companyId = UUID.randomUUID()
        Address address = new Address(
                id: addressId,
                city: 'São Paulo',
                state: 'SP')
        Company publisher = new Company(
                userId: companyId,
                user: new User(id: companyId),
                cnpj: '12.345.678/0001-90')
        Job job = new Job(
                id: jobId,
                name: 'Java Developer',
                description: 'Backend development',
                address: address,
                publisher: publisher)

        when:
        JobResponseDTO result = JobResponseDTO.fromDomain(job)

        then:
        result.id == jobId
        result.name == 'Java Developer'
        result.description == 'Backend development'
        result.addressId == addressId
        result.publisherId == companyId
    }
}
