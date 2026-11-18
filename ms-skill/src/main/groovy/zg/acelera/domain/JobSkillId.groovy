package zg.acelera.domain

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import groovy.transform.EqualsAndHashCode

@Embeddable
@EqualsAndHashCode
class JobSkillId implements Serializable {
    @Column(name = "job_id")
    UUID jobId

    @Column(name = "skill_id")
    UUID skillId
}
