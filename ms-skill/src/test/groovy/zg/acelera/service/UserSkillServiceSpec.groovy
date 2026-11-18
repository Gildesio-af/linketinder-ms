package zg.acelera.service

import spock.lang.Specification
import spock.lang.Subject
import zg.acelera.domain.Skill
import zg.acelera.domain.SkillLevel
import zg.acelera.domain.UserSkill
import zg.acelera.domain.UserSkillId
import zg.acelera.dto.user_skill.UserSkillCreateDTO
import zg.acelera.dto.user_skill.UserSkillResponseDTO
import zg.acelera.dto.user_skill.UserSkillUpdateDTO
import zg.acelera.repositories.skill.SkillRepository
import zg.acelera.repositories.user_skill.UserSkillRepository
import zg.acelera.utils.exception.EntityNotFoundException

class UserSkillServiceSpec extends Specification {

    UserSkillRepository userSkillRepository = Mock()
    SkillRepository skillRepository = Mock()

    @Subject
    UserSkillService service = new UserSkillService(userSkillRepository, skillRepository)

    UUID userId
    UUID skillId

    def setup() {
        userId = UUID.randomUUID()
        skillId = UUID.randomUUID()
    }

    def "findByUserId returns the mapped skills"() {
        given:
        Skill skill = new Skill(id: skillId, name: "Java")
        UserSkill userSkill = new UserSkill(
                id: new UserSkillId(userId: userId, skillId: skillId),
                skill: skill,
                level: SkillLevel.ADVANCED)
        userSkillRepository.findByUserId(userId) >> [userSkill]

        when:
        List<UserSkillResponseDTO> response = service.findByUserId(userId)

        then:
        response.size() == 1
        response[0].userId == userId
        response[0].skillId == skillId
        response[0].skillName == "Java"
        response[0].level == SkillLevel.ADVANCED
    }

    def "findById returns the requested user skill"() {
        given:
        UserSkill userSkill = createUserSkill(SkillLevel.INTERMEDIATE)
        userSkillRepository.findByUserIdAndSkillId(_ as UserSkillId) >> Optional.of(userSkill)

        when:
        UserSkillResponseDTO response = service.findById(userId, skillId)

        then:
        response.userId == userId
        response.skillId == skillId
        response.level == SkillLevel.INTERMEDIATE
    }

    def "findById throws when the user skill does not exist"() {
        given:
        userSkillRepository.findByUserIdAndSkillId(_ as UserSkillId) >> Optional.empty()

        when:
        service.findById(userId, skillId)

        then:
        thrown(EntityNotFoundException)
    }

    def "save creates and returns a user skill"() {
        given:
        UserSkillCreateDTO dto = new UserSkillCreateDTO(skillId: skillId, level: SkillLevel.BEGINNER)
        Skill skill = new Skill(id: skillId, name: "Java")
        userSkillRepository.findByUserIdAndSkillId(_ as UserSkillId) >> Optional.empty()
        skillRepository.findById(skillId) >> Optional.of(skill)
        userSkillRepository.save(_ as UserSkill) >> { UserSkill userSkill ->
            userSkill
        }

        when:
        UserSkillResponseDTO response = service.save(userId, dto)

        then:
        response.userId == userId
        response.skillId == skillId
        response.skillName == "Java"
        response.level == SkillLevel.BEGINNER
    }

    def "save rejects an already associated skill"() {
        given:
        UserSkillCreateDTO dto = new UserSkillCreateDTO(skillId: skillId, level: SkillLevel.ADVANCED)
        UserSkill existing = createUserSkill(SkillLevel.BEGINNER)
        userSkillRepository.findByUserIdAndSkillId(_ as UserSkillId) >> Optional.of(existing)

        when:
        service.save(userId, dto)

        then:
        IllegalArgumentException exception = thrown(IllegalArgumentException)
        exception.message == "Skill is already associated with user"
        0 * skillRepository.findById(_)
        0 * userSkillRepository.save(_)
    }

    def "save throws when the skill does not exist"() {
        given:
        UserSkillCreateDTO dto = new UserSkillCreateDTO(skillId: skillId, level: SkillLevel.BEGINNER)
        userSkillRepository.findByUserIdAndSkillId(_ as UserSkillId) >> Optional.empty()
        skillRepository.findById(skillId) >> Optional.empty()

        when:
        service.save(userId, dto)

        then:
        EntityNotFoundException exception = thrown(EntityNotFoundException)
        exception.message == "Skill not found with id: $skillId"
        0 * userSkillRepository.save(_)
    }

    def "update changes the skill level"() {
        given:
        UserSkill userSkill = createUserSkill(SkillLevel.BEGINNER)
        UserSkillUpdateDTO dto = new UserSkillUpdateDTO(level: SkillLevel.ADVANCED)
        userSkillRepository.findByUserIdAndSkillId(_ as UserSkillId) >> Optional.of(userSkill)
        userSkillRepository.save(_ as UserSkill) >> { UserSkill saved -> saved }

        when:
        UserSkillResponseDTO response = service.update(userId, skillId, dto)

        then:
        response.level == SkillLevel.ADVANCED
        userSkill.level == SkillLevel.ADVANCED
    }

    def "update throws when the user skill does not exist"() {
        given:
        UserSkillUpdateDTO dto = new UserSkillUpdateDTO(level: SkillLevel.ADVANCED)
        userSkillRepository.findByUserIdAndSkillId(_ as UserSkillId) >> Optional.empty()

        when:
        service.update(userId, skillId, dto)

        then:
        thrown(EntityNotFoundException)
        0 * userSkillRepository.save(_)
    }

    def "delete removes an existing user skill"() {
        given:
        UserSkill existing = createUserSkill(SkillLevel.BEGINNER)
        userSkillRepository.findByUserIdAndSkillId(_ as UserSkillId) >> Optional.of(existing)

        when:
        service.delete(userId, skillId)

        then:
        1 * userSkillRepository.delete({ UserSkillId id ->
            id.userId == userId && id.skillId == skillId
        })
    }

    def "delete throws when the user skill does not exist"() {
        given:
        userSkillRepository.findByUserIdAndSkillId(_ as UserSkillId) >> Optional.empty()

        when:
        service.delete(userId, skillId)

        then:
        def exception = thrown(EntityNotFoundException)
        exception.message == "User skill not found"
        0 * userSkillRepository.delete(_)
    }

    private UserSkill createUserSkill(SkillLevel level) {
        return new UserSkill(
                id: new UserSkillId(userId: userId, skillId: skillId),
                skill: new Skill(id: skillId, name: "Java"),
                level: level)
    }
}
