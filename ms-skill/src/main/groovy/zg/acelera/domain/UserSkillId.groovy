package zg.acelera.domain

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import groovy.transform.EqualsAndHashCode

@Embeddable
@EqualsAndHashCode
class UserSkillId implements Serializable {
    @Column(name = "user_id")
    UUID userId

    @Column(name = "skill_id")
    UUID skillId
}
