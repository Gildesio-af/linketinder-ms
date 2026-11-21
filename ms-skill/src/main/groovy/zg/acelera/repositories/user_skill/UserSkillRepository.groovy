package zg.acelera.repositories.user_skill

import zg.acelera.domain.UserSkill
import zg.acelera.domain.UserSkillId

interface UserSkillRepository {
    List<UserSkill> findByUserId(UUID userId)
    Optional<UserSkill> findByUserIdAndSkillId(UserSkillId id)
    UserSkill save(UserSkill userSkill)
    List<UserSkill> saveAll(List<UserSkill> userSkills)
    void delete(UserSkillId id)
}