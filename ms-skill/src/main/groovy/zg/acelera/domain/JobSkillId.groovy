package zg.acelera.domain

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
class JobSkillId implements Serializable {
    @Column(name = "job_id")
    UUID jobId

    @Column(name = "skill_id")
    UUID skillId
}
