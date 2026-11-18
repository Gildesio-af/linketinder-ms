package zg.acelera.repositories.user_skill

import zg.acelera.domain.UserSkill
import zg.acelera.domain.UserSkillId

interface UserSkillRepository {
    List<UserSkill> findByUserId(UUID userId)
    Optional<UserSkill> findByUserIdAndSkillId(UserSkillId id)
    UserSkill save(UserSkill userSkill)
    void delete(UserSkillId id)
}