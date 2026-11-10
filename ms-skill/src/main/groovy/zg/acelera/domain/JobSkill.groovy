package zg.acelera.domain

import jakarta.persistence.Column
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.Enumerated
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.MapsId
import jakarta.persistence.Table
import jakarta.persistence.EnumType
import jakarta.persistence.FetchType

@Entity
@Table(name = "jobs_skill")
class JobSkill {
    @EmbeddedId
    JobSkillId id = new JobSkillId()

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("skillId")
    @JoinColumn(name = "skill_id")
    Skill skill

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    SkillLevel level = SkillLevel.BASICO
}