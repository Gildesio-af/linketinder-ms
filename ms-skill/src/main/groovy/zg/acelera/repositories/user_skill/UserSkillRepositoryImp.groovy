package zg.acelera.repositories.user_skill

import org.springframework.stereotype.Repository
import zg.acelera.domain.UserSkill
@Repository
class UserSkillRepositoryImp implements UserSkillRepository {
    private final UserSkillRepositoryJpa userSkillRepositoryJpa

    UserSkillRepositoryImp(UserSkillRepositoryJpa userSkillRepositoryJpa) {
        this.userSkillRepositoryJpa = userSkillRepositoryJpa
    }

    @Override
    List<UserSkill> findByUserId(UUID userId) {
//        return userSkillRepositoryJpa.findByUserId(userId)
        return List.of()
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
    void delete(UserSkillId id) {
        userSkillRepositoryJpa.deleteById(id)
    }
}

import zg.acelera.domain.UserSkillId
