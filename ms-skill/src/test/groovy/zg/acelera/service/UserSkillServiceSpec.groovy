package zg.acelera.service

import spock.lang.Specification
import spock.lang.Subject
import zg.acelera.domain.Skill
import zg.acelera.domain.SkillLevel
import zg.acelera.domain.UserSkill
import zg.acelera.domain.UserSkillId
import zg.acelera.dto.skill.SkillAssociationCreateDTO
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
        SkillAssociationCreateDTO dto = new SkillAssociationCreateDTO(skillId: skillId, level: SkillLevel.BEGINNER)
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
        SkillAssociationCreateDTO dto = new SkillAssociationCreateDTO(skillId: skillId, level: SkillLevel.ADVANCED)
        UserSkill existing = createUserSkill(SkillLevel.BEGINNER)
        userSkillRepository.findByUserIdAndSkillId(_ as UserSkillId) >> Optional.of(existing)

        when:
        service.save(userId, dto)

        then:
        zg.acelera.utils.exception.DuplicateResourceException exception = thrown()
        exception.message == "Skill is already associated with user"
        0 * skillRepository.findById(_)
        0 * userSkillRepository.save(_)
    }

    def "save throws when the skill does not exist"() {
        given:
        SkillAssociationCreateDTO dto = new SkillAssociationCreateDTO(skillId: skillId, level: SkillLevel.BEGINNER)
        userSkillRepository.findByUserIdAndSkillId(_ as UserSkillId) >> Optional.empty()
        skillRepository.findById(skillId) >> Optional.empty()

        when:
        service.save(userId, dto)

        then:
        EntityNotFoundException exception = thrown(EntityNotFoundException)
        exception.message == "Skill not found with id: $skillId"
        0 * userSkillRepository.save(_)
    }

    def "saveAll creates every skill"() {
        given:
        UUID secondSkillId = UUID.randomUUID()
        def java = new SkillAssociationCreateDTO(skillId: skillId, level: SkillLevel.BEGINNER)
        def groovy = new SkillAssociationCreateDTO(skillId: secondSkillId, level: SkillLevel.ADVANCED)
        def javaSkill = new Skill(id: skillId, name: "Java")
        def groovySkill = new Skill(id: secondSkillId, name: "Groovy")
        def savedUserSkills = [
                new UserSkill(
                        id: new UserSkillId(userId: userId, skillId: skillId),
                        skill: javaSkill,
                        level: SkillLevel.BEGINNER),
                new UserSkill(
                        id: new UserSkillId(userId: userId, skillId: secondSkillId),
                        skill: groovySkill,
                        level: SkillLevel.ADVANCED)
        ]
        skillRepository.findAllById([skillId, secondSkillId]) >> [javaSkill, groovySkill]
        userSkillRepository.findByUserId(userId) >> []
        userSkillRepository.saveAll(_ as List<UserSkill>) >> savedUserSkills

        when:
        def response = service.saveAll(userId, [java, groovy])

        then:
        response*.userId == [userId, userId]
        response*.skillId == [skillId, secondSkillId]
        response*.level == [SkillLevel.BEGINNER, SkillLevel.ADVANCED]
    }

    def "saveAll rejects an empty batch"() {
        when:
        service.saveAll(userId, [])

        then:
        IllegalArgumentException exception = thrown()
        exception.message == "At least one user skill is required"
        0 * userSkillRepository._
        0 * skillRepository._
    }

    def "saveAll rejects duplicate skill ids"() {
        given:
        def dto = new SkillAssociationCreateDTO(skillId: skillId, level: SkillLevel.BEGINNER)

        when:
        service.saveAll(userId, [dto, dto])

        then:
        zg.acelera.utils.exception.DuplicateResourceException exception = thrown()
        exception.message == "Duplicate skill id in batch: $skillId"
        0 * userSkillRepository._
        0 * skillRepository._
    }

    def "saveAll rejects a null skill id"() {
        given:
        def dto = new SkillAssociationCreateDTO(skillId: null, level: SkillLevel.BEGINNER)

        when:
        service.saveAll(userId, [dto])

        then:
        IllegalArgumentException exception = thrown()
        exception.message == "Skill id cannot be null"
        0 * userSkillRepository._
        0 * skillRepository._
    }

    def "saveAll rejects an already associated skill"() {
        given:
        def dto = new SkillAssociationCreateDTO(skillId: skillId, level: SkillLevel.BEGINNER)
        skillRepository.findAllById([skillId]) >> [new Skill(id: skillId, name: "Java")]
        userSkillRepository.findByUserId(userId) >>
                [createUserSkill(SkillLevel.BEGINNER)]

        when:
        service.saveAll(userId, [dto])

        then:
        zg.acelera.utils.exception.DuplicateResourceException exception = thrown()
        exception.message == "Skill is already associated with user"
        0 * userSkillRepository.saveAll(_)
    }

    def "saveAll is atomic when one skill does not exist"() {
        given:
        UUID missingSkillId = UUID.randomUUID()
        def existing = new SkillAssociationCreateDTO(skillId: skillId, level: SkillLevel.BEGINNER)
        def missing = new SkillAssociationCreateDTO(skillId: missingSkillId, level: SkillLevel.ADVANCED)
        skillRepository.findAllById(_ as List<UUID>) >>
                [new Skill(id: skillId, name: "Java")]
        userSkillRepository.findByUserId(userId) >> []

        when:
        service.saveAll(userId, [existing, missing])

        then:
        thrown(EntityNotFoundException)
        0 * userSkillRepository.saveAll(_)
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
