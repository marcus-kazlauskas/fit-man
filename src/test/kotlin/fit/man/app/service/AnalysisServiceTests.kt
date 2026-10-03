package fit.man.app.service

import fit.man.app.config.AppProperties
import fit.man.app.config.AppThreadPoolConfig
import fit.man.app.fixtures.ActivityFixtures
import fit.man.app.repository.ActivityRepository
import fit.man.app.repository.AnalysisRepository
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

@Import(AnalysisService::class, ActivityService::class, AppThreadPoolConfig::class)
@EnableConfigurationProperties(AppProperties::class)
@SpringJUnitConfig
class AnalysisServiceTests {
    @Autowired
    lateinit var analysisService: AnalysisService

    @Autowired
    lateinit var activityService: ActivityService

    @MockitoBean
    lateinit var activityRepository: ActivityRepository

    @MockitoBean
    lateinit var analysisRepository: AnalysisRepository

    @Test
    fun shouldAnalyzeActivity() {
        val activity = ActivityFixtures.createNewActivity()

        whenever(
            activityRepository.findByMarkedTrueAndAnalysisIsNull(any<PageRequest>()),
        ).thenReturn(listOf(activity))

        analysisService.runAnalysis()
    }

    @Test
    fun shouldCalcZeroDistance() {
        val activity = ActivityFixtures.createNewActivity()
        activity.records = mutableListOf()
        activity.events = mutableListOf()

        val totalDistance = AnalysisService.calcTotalDistance(activity.records)
        val movingTime = AnalysisService.calcMovingTime(activity.records, activity.events)

        assertThat(Math.round(totalDistance)).isEqualTo(0L)
        assertThat(Math.round(movingTime)).isEqualTo(0L)
    }

    @Test
    fun shouldCalcTotalDistance() {
        val activity = ActivityFixtures.createNewActivity()
        activity.records = mutableListOf()
        activity.addRecord(ActivityFixtures.createRecordWithMarkDisabled())
        activity.addRecord(ActivityFixtures.createRecord1())
        activity.addRecord(ActivityFixtures.createRecordWithMarkDisabled())
        activity.addRecord(ActivityFixtures.createRecord2())

        val totalDistance = AnalysisService.calcTotalDistance(activity.records)

        assertThat(Math.round(totalDistance)).isEqualTo(12776L)
    }

    @Test
    fun shouldCalcMovingTime() {
        val activity = ActivityFixtures.createNewActivity()
        activity.records = mutableListOf()
        activity.addRecord(ActivityFixtures.createRecordWithMarkDisabled())
        activity.addRecord(ActivityFixtures.createRecord1())
        activity.addRecord(ActivityFixtures.createRecordWithMarkDisabled())
        activity.addRecord(ActivityFixtures.createRecord2())
        activity.addRecord(ActivityFixtures.createRecord3())
        activity.addRecord(ActivityFixtures.createRecord4())
        activity.events = mutableListOf()
        activity.addEvent(ActivityFixtures.createEvent1())
        activity.addEvent(ActivityFixtures.createDisabledEvent())
        activity.addEvent(ActivityFixtures.createEvent2())
        activity.addEvent(ActivityFixtures.createEvent3())
        activity.addEvent(ActivityFixtures.createEvent4())

        val movingTime = AnalysisService.calcMovingTime(activity.records, activity.events)

        assertThat(Math.round(movingTime)).isEqualTo(1380L)
    }
}
