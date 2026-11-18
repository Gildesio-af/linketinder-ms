package zg.acelera.repositories.user_skill

import spock.lang.Specification
import spock.lang.Subject
import zg.acelera.domain.Skill
import zg.acelera.domain.SkillLevel
import zg.acelera.domain.UserSkill
import zg.acelera.domain.UserSkillId

class UserSkillRepositoryImpSpec extends Specification {

    UserSkillRepositoryJpa jpaRepository = Mock()

    @Subject
    UserSkillRepositoryImp repository = new UserSkillRepositoryImp(jpaRepository)

    def "findByUserId delegates to the JPA repository"() {
        given:
        UUID userId = UUID.randomUUID()
        UserSkill userSkill = createUserSkill(userId)
        jpaRepository.findAllByIdUserId(userId) >> [userSkill]

        expect:
        repository.findByUserId(userId) == [userSkill]
    }

    def "findByUserIdAndSkillId delegates to the JPA repository"() {
        given:
        UUID userId = UUID.randomUUID()
        UserSkillId id = new UserSkillId(userId: userId, skillId: UUID.randomUUID())
        UserSkill userSkill = createUserSkill(userId)
        jpaRepository.findById(id) >> Optional.of(userSkill)

        expect:
        repository.findByUserIdAndSkillId(id) == Optional.of(userSkill)
    }

    def "save delegates to the JPA repository"() {
        given:
        UserSkill userSkill = createUserSkill(UUID.randomUUID())
        jpaRepository.save(userSkill) >> userSkill

        expect:
        repository.save(userSkill) == userSkill
    }

    def "delete delegates to the JPA repository"() {
        given:
        UserSkillId id = new UserSkillId(
                userId: UUID.randomUUID(),
                skillId: UUID.randomUUID())

        when:
        repository.delete(id)

        then:
        1 * jpaRepository.deleteById(id)
    }

    private UserSkill createUserSkill(UUID userId) {
        UUID skillId = UUID.randomUUID()
        return new UserSkill(
                id: new UserSkillId(userId: userId, skillId: skillId),
                skill: new Skill(id: skillId, name: "Java"),
                level: SkillLevel.BEGINNER)
    }
}
