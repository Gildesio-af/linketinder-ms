package zg.acelera.repositories.skill

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import zg.acelera.domain.Skill

@Repository
class SkillRepositoryImp implements SkillRepository {
    private final SkillRepositoryJpa skillRepositoryJpa

    SkillRepositoryImp(SkillRepositoryJpa skillRepositoryJpa) {
        this.skillRepositoryJpa = skillRepositoryJpa
    }

    @Override
    Optional<Skill> findById(UUID id) {
        return skillRepositoryJpa.findById(id)
    }

    @Override
    Page<Skill> findAll(Pageable pageable) {
        return skillRepositoryJpa.findAll(pageable)
    }

    @Override
    Optional<Skill> findByNameIgnoreCase(String name) {
        return skillRepositoryJpa.findByNameIgnoreCase(name)
    }

    @Override
    Skill save(Skill skill) {
        return skillRepositoryJpa.save(skill)
    }

    @Override
    boolean existsById(UUID id) {
        return skillRepositoryJpa.existsById(id)
    }

    @Override
    void delete(UUID id) {
        skillRepositoryJpa.deleteById(id)
    }
}
