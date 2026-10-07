package fit.man.app.service

import fit.man.app.config.AppProperties
import fit.man.app.fixtures.ActivityFixtures
import fit.man.app.repository.ActivityRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Import
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import java.time.OffsetDateTime
import java.util.Optional

@Import(MarkupService::class, ActivityService::class)
@EnableConfigurationProperties(AppProperties::class)
@SpringJUnitConfig
class MarkupServiceTests {
    @Autowired
    lateinit var markupService: MarkupService

    @Autowired
    lateinit var activityService: ActivityService

    @MockitoBean
    lateinit var activityRepository: ActivityRepository

    @Test
    fun shouldMarkActivity() {
        val activity = ActivityFixtures.createNewActivity()
        activity.records = mutableListOf()
        activity.addRecord(ActivityFixtures.createRecordWithNullLat())
        activity.addRecord(ActivityFixtures.createRecordWithNullLong())
        activity.addRecord(ActivityFixtures.createRecord1())
        activity.addRecord(ActivityFixtures.createRecordWithFarPos())
        activity.addRecord(ActivityFixtures.createRecordWithNullLat())
        activity.addRecord(ActivityFixtures.createRecordWithNullLong())
        activity.addRecord(ActivityFixtures.createRecord2())
        activity.addRecord(ActivityFixtures.createRecord2())

        whenever(activityRepository.findByMarkedFalse(any<PageRequest>()))
            .thenReturn(listOf(activity))

        markupService.runMarkup()

        whenever(
            activityRepository.findFirstByStartTimeBetweenOrderByStartTime(
                any<OffsetDateTime>(),
                any<OffsetDateTime>(),
            ),
        ).thenReturn(Optional.of(activity))

        val track = activityService.getTrackInRange("2026-04-26T13:12:00", "2026-04-26T13:12:00")

        assertThat(track).isNotNull()
        assertThat(track.points).isNotEmpty().hasSize(3)
    }
}
