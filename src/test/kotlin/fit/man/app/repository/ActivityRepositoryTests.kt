package fit.man.app.repository

import fit.man.app.fixtures.ActivityFixtures
import fit.man.app.repository.entity.Activity
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ActivityRepositoryTests {
    @Autowired
    lateinit var activityRepository: ActivityRepository

    lateinit var activity: Activity

    @BeforeEach
    fun setUp() {
        activity = ActivityFixtures.createNewActivity()
    }

    @Test
    fun shouldSaveActivity() {
        val savedActivity = activityRepository.save(activity)

        assertThat(savedActivity.id).isNotNegative()
        assertThat(savedActivity.records).isNotEmpty()
        assertThat(savedActivity.records.first()).isNotNull()
    }

    @Test
    fun shouldCheckActivityExists() {
        activityRepository.save(activity)
        val exists = activityRepository.existsByStartTime(ActivityFixtures.START_TIME)

        assertThat(exists).isEqualTo(true)
    }

    @Test
    fun shouldFindActivityInRange() {
        activityRepository.save(activity)
        val foundActivity =
            activityRepository.findFirstByStartTimeBetweenOrderByStartTime(
                ActivityFixtures.START_TIME.minusMinutes(1),
                ActivityFixtures.START_TIME.plusMinutes(1),
            )

        assertThat(foundActivity).isPresent()
        assertThat(foundActivity.get().id).isNotNegative()
    }
}
