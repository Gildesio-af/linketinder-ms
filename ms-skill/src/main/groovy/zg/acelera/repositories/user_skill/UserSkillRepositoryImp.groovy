package zg.acelera.repositories.user_skill

import org.springframework.stereotype.Repository
import zg.acelera.domain.UserSkill
import zg.acelera.domain.UserSkillId
@Repository
class UserSkillRepositoryImp implements UserSkillRepository {
    private final UserSkillRepositoryJpa userSkillRepositoryJpa

    UserSkillRepositoryImp(UserSkillRepositoryJpa userSkillRepositoryJpa) {
        this.userSkillRepositoryJpa = userSkillRepositoryJpa
    }

    @Override
    List<UserSkill> findByUserId(UUID userId) {
        return userSkillRepositoryJpa.findAllByIdUserId(userId)
    }


    @Override
    List<UserSkill> findByUserIds(Set<UUID> userIds) {
        return userSkillRepositoryJpa.findAllByUserIds(userIds)
    }

    @Override
    Optional<UserSkill> findByUserIdAndSkillId(UserSkillId id) {
        return userSkillRepositoryJpa.findById(id)
    }

    @Override
    UserSkill save(UserSkill userSkill) {
        return userSkillRepositoryJpa.save(userSkill)
    }

    @Override
    List<UserSkill> saveAll(List<UserSkill> userSkills) {
        return userSkillRepositoryJpa.saveAll(userSkills)
    }

    @Override
    void delete(UserSkillId id) {
        userSkillRepositoryJpa.deleteById(id)
    }
}
