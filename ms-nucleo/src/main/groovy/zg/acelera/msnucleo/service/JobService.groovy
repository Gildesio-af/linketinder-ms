package zg.acelera.msnucleo.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import zg.acelera.msnucleo.domain.Address
import zg.acelera.msnucleo.domain.Company
import zg.acelera.msnucleo.domain.Job
import zg.acelera.msnucleo.dto.job.JobCreateDTO
import zg.acelera.msnucleo.dto.job.JobResponseDTO
import zg.acelera.msnucleo.dto.job.JobUpdateDTO
import zg.acelera.msnucleo.dto.ms_skill.SkillResponseDTO
import zg.acelera.msnucleo.infrastructure.client.SkillClient
import zg.acelera.msnucleo.repositories.address.AddressRepository
import zg.acelera.msnucleo.repositories.company.CompanyRepository
import zg.acelera.msnucleo.repositories.job.JobRepository
import zg.acelera.msnucleo.utils.exception.EntityNotFoundException

@Service
class JobService {
    private final JobRepository jobRepository
    private final AddressRepository addressRepository
    private final CompanyRepository companyRepository
    private final SkillClient skillClient

    JobService(JobRepository jobRepository,
               AddressRepository addressRepository,
               CompanyRepository companyRepository,
               SkillClient skillClient) {
        this.jobRepository = jobRepository
        this.addressRepository = addressRepository
        this.companyRepository = companyRepository
        this.skillClient = skillClient
    }

    @Transactional(readOnly = true)
    JobResponseDTO findById(UUID id) {
        Job job = findEntity(id)
        JobResponseDTO.fromDomain(job, skillClient.getSkillsByJobId(job.id))
    }

    @Transactional(readOnly = true)
    Page<JobResponseDTO> findAll(Pageable pageable) {
        withSkills(jobRepository.findAll(pageable))
    }

    @Transactional(readOnly = true)
    Page<JobResponseDTO> findByPublisherId(UUID publisherId, Pageable pageable) {
        findPublisher(publisherId)
        withSkills(jobRepository.findByPublisherId(publisherId, pageable))
    }

    @Transactional
    JobResponseDTO save(JobCreateDTO dto) {
        Job job = new Job(
                name: dto.name.trim(),
                description: dto.description.trim(),
                address: findAddress(dto.addressId),
                publisher: findPublisher(dto.publisherId))
        JobResponseDTO.fromDomain(jobRepository.save(job))
    }

    @Transactional
    JobResponseDTO update(UUID id, JobUpdateDTO dto) {
        Job job = findEntity(id)
        if (dto.name != null)
            job.name = dto.name.trim()
        if (dto.description != null)
            job.description = dto.description.trim()
        if (dto.addressId != null)
            job.address = findAddress(dto.addressId)
        JobResponseDTO.fromDomain(jobRepository.save(job))
    }

    @Transactional
    void delete(UUID id) {
        if (!jobRepository.existsById(id))
            throw new EntityNotFoundException("Job not found with id: $id")
        jobRepository.delete(id)
    }

    private Job findEntity(UUID id) {
        jobRepository.findById(id)
                .orElseThrow { new EntityNotFoundException("Job not found with id: $id") }
    }

    private Page<JobResponseDTO> withSkills(Page<Job> page) {
        if (page.isEmpty())
            return page.map { JobResponseDTO.fromDomain(it) }
        Map<UUID, List<SkillResponseDTO>> skillsByJob = [:]
        page.content.collect { it.id }.unique().collate(50).each { batch ->
            skillsByJob.putAll(skillClient.getSkillsByJobIds(batch))
        }
        page.map { job -> JobResponseDTO.fromDomain(job, skillsByJob.getOrDefault(job.id, [])) }
    }

    private Address findAddress(UUID id) {
        addressRepository.findById(id)
                .orElseThrow { new EntityNotFoundException("Address not found with id: $id") }
    }

    private Company findPublisher(UUID id) {
        companyRepository.findById(id)
                .orElseThrow { new EntityNotFoundException("Company not found with id: $id") }
    }
}
