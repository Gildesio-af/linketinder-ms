package zg.acelera.repositories.skill


import org.springframework.data.jpa.repository.JpaRepository
import zg.acelera.domain.Skill

interface SkillRepositoryJpa extends JpaRepository<Skill, UUID> {
}
