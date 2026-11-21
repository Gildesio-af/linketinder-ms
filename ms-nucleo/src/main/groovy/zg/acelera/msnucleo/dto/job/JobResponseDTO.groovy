package zg.acelera.msnucleo.dto.job

import zg.acelera.msnucleo.domain.Job
import zg.acelera.msnucleo.dto.ms_skill.SkillResponseDTO

class JobResponseDTO {
    UUID id
    String name
    String description
    UUID addressId
    UUID publisherId
    List<SkillResponseDTO> skills = []

    static JobResponseDTO fromDomain(Job job, List<SkillResponseDTO> skills = []) {
        new JobResponseDTO(
                id: job.id,
                name: job.name,
                description: job.description,
                addressId: job.address.id,
                publisherId: job.publisher.userId,
                skills: skills)
    }
}
