package zg.acelera.msnucleo.dto.job

import zg.acelera.msnucleo.domain.Job

class JobResponseDTO {
    UUID id
    String name
    String description
    UUID addressId
    UUID publisherId

    static JobResponseDTO fromDomain(Job job) {
        new JobResponseDTO(
                id: job.id,
                name: job.name,
                description: job.description,
                addressId: job.address.id,
                publisherId: job.publisher.userId)
    }
}
