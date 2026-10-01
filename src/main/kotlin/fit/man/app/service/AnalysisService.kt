package fit.man.app.service

import com.garmin.fit.EventType
import fit.man.app.config.AppProperties
import fit.man.app.repository.AnalysisRepository
import fit.man.app.repository.entity.Activity
import fit.man.app.repository.entity.Analysis
import fit.man.app.repository.entity.Event
import fit.man.app.repository.entity.Record
import fit.man.app.util.ActivityUtils
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.temporal.ChronoUnit
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit

@Service
class AnalysisService(
    private val activityService: ActivityService,
    private val analysisRepository: AnalysisRepository,
    private val appProperties: AppProperties,
    @Qualifier("analysisExecutor") private val analysisExecutor: Executor
) {
    fun runAnalysis() {
        val activities = activityService.findActivitiesForAnalysis()
        for (activity in activities) {
            analyzeAnSave(activity)
        }
    }

    private fun analyzeAnSave(activity: Activity) {
        val analysis = Analysis()
        analysis.activity = activity

        val records = activity.records
        val events = activity.events

        val futureTotalDistance: CompletableFuture<Double> = CompletableFuture.supplyAsync(
            { calcTotalDistance(records) }, analysisExecutor
        )

        val futureMovingTime: CompletableFuture<Double> = CompletableFuture.supplyAsync(
            { calcMovingTime(records, events) }, analysisExecutor
        )

        try {
            CompletableFuture.allOf(futureTotalDistance, futureMovingTime)
                .orTimeout(appProperties.activityScheduler.timeout.toLong(), TimeUnit.SECONDS)
                .join()

            val totalDistance = futureTotalDistance.join()
            val movingTime = futureMovingTime.join()

            analysis.totalDistance = totalDistance.toFloat()
            analysis.movingTime = movingTime.toLong()
            analysis.averageSpeed = if (movingTime > 0)
                (totalDistance / movingTime * ActivityUtils.KM_PER_HOUR).toFloat()
            else 0f
            analysis.success = true
        } catch (e: RuntimeException) {
            analysis.success = false
            log.atWarn().log("Exception occurred during analysis: ", e)
        }

        analysisRepository.save(analysis)
        log.atInfo().log("Saved analysis {}", analysis)
    }

    companion object {
        private val log = LoggerFactory.getLogger(AnalysisService::class.java)

        @JvmStatic
        fun calcTotalDistance(records: List<Record>): Double {
            if (records.isEmpty()) {
                return 0.0
            }

            var totalDistance = 0.0
            var i = 0
            var j = 1
            while (j < records.size) {
                val record1 = records[i]
                val record2 = records[j]
                if (record1.mark == ActivityUtils.MARK_DISABLED) {
                    i++
                } else if (record2.mark == ActivityUtils.MARK_DEFAULT) {
                    totalDistance += ActivityUtils.calcDistance(
                        record1.positionLat!!,
                        record1.positionLong!!,
                        record2.positionLat!!,
                        record2.positionLong!!
                    )
                    i = j
                }
                j++
            }
            return totalDistance
        }

        @JvmStatic
        fun calcMovingTime(records: List<Record>, events: List<Event>): Double {
            if (records.isEmpty() || events.isEmpty()) {
                return 0.0
            }

            var trackTime = 0.0
            var i = 0
            var j = 1

            val validEvents = mutableListOf<Event>()
            var k = 0

            while (j < records.size) {
                val record1 = records[i]
                val record2 = records[j]
                if (record1.mark == ActivityUtils.MARK_DISABLED) {
                    i++
                } else if (record2.mark == ActivityUtils.MARK_DEFAULT) {
                    trackTime += Duration.between(
                        record1.positionTime,
                        record2.positionTime
                    ).toMillis() / ActivityUtils.MILLIS.toDouble()
                    i = j

                    if (k < events.size) {
                        var event = events[k]
                        var eventTime = event.eventTime!!.truncatedTo(ChronoUnit.SECONDS)
                        val validTime = record1.positionTime!!.truncatedTo(ChronoUnit.SECONDS)

                        while (k < events.size - 1 && eventTime.isBefore(validTime)) {
                            k++
                            event = events[k]
                            eventTime = event.eventTime!!.truncatedTo(ChronoUnit.SECONDS)
                        }

                        if (eventTime == validTime) {
                            validEvents.add(event)
                            k++
                        }
                    }
                }
                j++
            }
            log.atInfo().log("Collected valid {} timer events {}", validEvents.size, validEvents)

            var idlingTime = 0.0
            k = 0
            var l = 1
            while (l < validEvents.size) {
                val event1 = validEvents[k]
                val event2 = validEvents[l]
                if (event1.eventType == EventType.START.name) {
                    k++
                    l++
                } else if (event2.eventType == EventType.STOP.name || event2.eventType == EventType.STOP_ALL.name) {
                    l++
                } else {
                    idlingTime += Duration.between(
                        event1.eventTime,
                        event2.eventTime
                    ).toMillis() / ActivityUtils.MILLIS.toDouble()
                    k = l + 1
                    l = k + 1
                }
            }
            val movingTime = trackTime - idlingTime
            log.atInfo().log(
                "Result: trackTime[{}] - idlingTime[{}] = movingTime[{}]",
                trackTime, idlingTime, movingTime
            )

            return movingTime
        }
    }
}
