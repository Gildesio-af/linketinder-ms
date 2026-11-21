package zg.acelera.repositories.user_skill

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import zg.acelera.domain.UserSkill
import zg.acelera.domain.UserSkillId

interface UserSkillRepositoryJpa extends JpaRepository<UserSkill, UserSkillId> {
    List<UserSkill> findAllByIdUserId(UUID userId)

    @Query("SELECT us FROM UserSkill us JOIN FETCH us.skill WHERE us.id.userId IN (:userIds)")
    List<UserSkill> findAllByUserIds(@Param("userIds") Set<UUID> userIds)
}