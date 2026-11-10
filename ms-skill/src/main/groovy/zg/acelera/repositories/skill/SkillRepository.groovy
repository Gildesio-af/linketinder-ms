package zg.acelera.repositories.skill

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import zg.acelera.domain.Skill

interface SkillRepository {
    Optional<Skill> findById(UUID id)
    Page<Skill> findAll(Pageable pageable)
    Skill save(Skill skill)
    void delete(UUID id)
}