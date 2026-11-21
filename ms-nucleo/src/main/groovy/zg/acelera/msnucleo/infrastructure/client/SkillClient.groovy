package zg.acelera.msnucleo.infrastructure.client

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import zg.acelera.msnucleo.dto.ms_skill.SkillResponseDTO

@FeignClient(name = "ms-skill", url = "\${clients.ms-skill.url}")
interface SkillClient {
    @GetMapping("/jobs/{jobId}/skills")
    List<SkillResponseDTO> getSkillsByJobId(@PathVariable("jobId") UUID jobId)

    @GetMapping("/jobs/skills/batch")
    Map<UUID, List<SkillResponseDTO>> getSkillsByJobIds(@RequestParam("jobIds") List<UUID> jobIds)

    @GetMapping("/users/skills/{userId}")
    List<SkillResponseDTO> getSkillsByUserId(@PathVariable("userId") UUID userId)

    @GetMapping("/users/skills/batch")
    Map<UUID, List<SkillResponseDTO>> getSkillsByUserIds(@RequestParam(name = "userIds", defaultValue = "") List<UUID> userIds)

}
