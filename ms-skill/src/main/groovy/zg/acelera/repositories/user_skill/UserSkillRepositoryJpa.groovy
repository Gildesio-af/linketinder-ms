package zg.acelera.repositories.user_skill

import org.springframework.data.jpa.repository.JpaRepository
import zg.acelera.domain.UserSkill
import zg.acelera.domain.UserSkillId

interface UserSkillRepositoryJpa extends JpaRepository<UserSkill, UserSkillId> {
//    List<UserSkill> findByUserId(UUID userId)
}