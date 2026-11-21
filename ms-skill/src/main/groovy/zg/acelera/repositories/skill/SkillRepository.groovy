package zg.acelera.repositories.skill

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import zg.acelera.domain.Skill

interface SkillRepository {
    Optional<Skill> findById(UUID id)
    List<Skill> findAllById(List<UUID> ids)
    Page<Skill> findAll(Pageable pageable)
    Optional<Skill> findByNameIgnoreCase(String name)
    Skill save(Skill skill)
    boolean existsById(UUID id)
    void delete(UUID id)
}