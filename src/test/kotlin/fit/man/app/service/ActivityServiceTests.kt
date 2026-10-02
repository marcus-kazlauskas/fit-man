package fit.man.app.service

import fit.man.app.advice.exception.ActivityNotFoundException
import fit.man.app.advice.exception.FitFileException
import fit.man.app.config.AppProperties
import fit.man.app.fixtures.ActivityFixtures
import fit.man.app.repository.ActivityRepository
import fit.man.app.repository.entity.Activity
import fit.man.app.util.ActivityUtils
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Import
import org.springframework.core.io.Resource
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import java.time.Duration
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.util.Optional

@Import(ActivityService::class)
@EnableConfigurationProperties(AppProperties::class)
@SpringJUnitConfig
class ActivityServiceTests {
    @Autowired
    lateinit var activityService: ActivityService

    @MockitoBean
    lateinit var activityRepository: ActivityRepository

    @Value("classpath:files/A24A93E6-A62E-466E-AF48-52F1ADD8684E.fit")
    lateinit var fitFile: Resource

    @Value("classpath:files/56CFD91A-E05E-43AA-B878-BDC089258240.png")
    lateinit var pngFile: Resource

    @Test
    fun shouldReadFitFile() {
        val activity = activityService.readFitFile(fitFile.inputStream)

        assertThat(activity).isNotNull()
        assertAll(
            { assertEquals(OffsetDateTime.parse("2025-07-05T14:07:18.000+03:00"), activity.endTime) },
            { assertEquals(OffsetDateTime.parse("2025-07-05T02:59:50.000+03:00"), activity.startTime) },
            { assertEquals("CYCLING", activity.sport) },
            { assertEquals(Duration.parse("PT11H07M27.784S"), activity.totalElapsedTime) },
            { assertEquals(Duration.parse("PT03H30M04.197S"), activity.totalTimerTime) },
            { assertEquals(157846.22F, activity.totalDistance) },
            { assertEquals(5744, activity.totalCalories) },
            { assertEquals(239, activity.totalAscent) },
            { assertEquals(12.523F, activity.enhancedAvgSpeed) },
            { assertEquals(23.842F, activity.enhancedMaxSpeed) },
            { assertEquals("Mikhail Kozlov", activity.userName) },
            { assertEquals("Cannondale App", activity.deviceName) },
            { assertFalse(activity.marked) }
        )
        assertThat(activity.records).isNotEmpty()
        val record = activity.records.first()
        assertAll(
            { assertEquals(LocalDateTime.parse("2025-07-04T23:59:50.000"), record.positionTime) },
            { assertEquals(ActivityUtils.MARK_DEFAULT, record.mark) }
        )
    }

    @Test
    fun shouldThrowExceptionWhenReadPngFile() {
        assertThatThrownBy { activityService.readFitFile(pngFile.inputStream) }
            .isInstanceOf(FitFileException::class.java)
    }

    @Test
    fun shouldLoadNewActivity() {
        whenever(activityRepository.save(any<Activity>()))
            .thenReturn(ActivityFixtures.createNewActivity())

        val response = activityService.loadNewActivity(fitFile.inputStream)

        assertThat(response).isNotNull()
        assertThat(response.startTime).isEqualTo(ActivityFixtures.START_TIME)
        assertThat(response.records).isNotEmpty()
    }

    @Test
    fun shouldThrowExceptionWhenActivityExists() {
        whenever(activityRepository.existsByStartTime(any<OffsetDateTime>()))
            .thenReturn(true)

        assertThatThrownBy { activityService.checkNotExistsAndSave(ActivityFixtures.createNewActivity()) }
            .isInstanceOf(FitFileException::class.java)
    }

    @Test
    fun shouldGetTrackInRange() {
        val activity = ActivityFixtures.createNewActivity()
        activity.addRecord(ActivityFixtures.createRecordWithNullLat())
        activity.addRecord(ActivityFixtures.createRecordWithNullLong())
        activity.addRecord(ActivityFixtures.createRecordWithMarkDisabled())

        whenever(
            activityRepository.findFirstByStartTimeBetweenOrderByStartTime(
                any<OffsetDateTime>(), any<OffsetDateTime>()
            )
        ).thenReturn(Optional.of(activity))

        val track = activityService.getTrackInRange("2026-04-26T13:12:00", "2026-04-26T13:12:00")

        assertThat(track).isNotNull()
        assertAll(
            { assertEquals(1, track.points!!.size) },
            { assertEquals("2026-04-23T13:12", track.startTime) },
            { assertEquals(86400L, track.totalElapsedTime) },
            { assertEquals(13.12F, track.totalDistance) },
            { assertEquals(47520L, track.movingTime) },
            { assertEquals(4F, track.averageSpeed) }
        )
    }

    @Test
    fun shouldGetTrackWithSuccessfulAnalysis() {
        val activity = ActivityFixtures.createNewActivity()
        val analysis = ActivityFixtures.createAnalysis()
        activity.analysis = analysis

        whenever(
            activityRepository.findFirstByStartTimeBetweenOrderByStartTime(
                any<OffsetDateTime>(), any<OffsetDateTime>()
            )
        ).thenReturn(Optional.of(activity))

        val track = activityService.getTrackInRange("2026-04-26T13:12:00", "2026-04-26T13:12:00")

        assertThat(track).isNotNull()
        assertAll(
            { assertEquals(1, track.points!!.size) },
            { assertEquals("2026-04-23T13:12", track.startTime) },
            { assertEquals(86400L, track.totalElapsedTime) },
            { assertEquals(13000F, track.totalDistance) },
            { assertEquals(43200L, track.movingTime) },
            { assertEquals(1.08F, track.averageSpeed) }
        )
    }

    @Test
    fun shouldGetTrackWithUnsuccessfulAnalysis() {
        val activity = ActivityFixtures.createNewActivity()
        val analysis = ActivityFixtures.createUnsuccessfulAnalysis()
        activity.analysis = analysis

        whenever(
            activityRepository.findFirstByStartTimeBetweenOrderByStartTime(
                any<OffsetDateTime>(), any<OffsetDateTime>()
            )
        ).thenReturn(Optional.of(activity))

        val track = activityService.getTrackInRange("2026-04-26T13:12:00", "2026-04-26T13:12:00")

        assertThat(track).isNotNull()
        assertAll(
            { assertEquals(1, track.points!!.size) },
            { assertEquals("2026-04-23T13:12", track.startTime) },
            { assertEquals(86400L, track.totalElapsedTime) },
            { assertEquals(13.12F, track.totalDistance) },
            { assertEquals(47520L, track.movingTime) },
            { assertEquals(4F, track.averageSpeed) }
        )
    }

    @Test
    fun shouldThrowExceptionWhenActivityNotFound() {
        whenever(
            activityRepository.findFirstByStartTimeBetweenOrderByStartTime(
                any<OffsetDateTime>(), any<OffsetDateTime>()
            )
        ).thenReturn(Optional.empty())

        assertThatThrownBy {
            activityService.getTrackInRange("2026-04-26T13:12:00", "2026-04-26T13:12:00")
        }.isInstanceOf(ActivityNotFoundException::class.java)
    }
}
