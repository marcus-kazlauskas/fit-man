package fit.man.app.service

import fit.man.app.config.AppProperties
import fit.man.app.repository.ActivityRepository
import fit.man.app.repository.entity.Activity
import fit.man.app.repository.entity.Record
import fit.man.app.util.ActivityUtils
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

@Service
@Transactional
class MarkupService(
    private val activityService: ActivityService,
    private val activityRepository: ActivityRepository,
    private val appProperties: AppProperties,
) {
    fun runMarkup() {
        val activities = activityService.findActivitiesForMarkup()
        for (activity in activities) {
            markAndSave(activity)
        }
    }

    private fun markAndSave(activity: Activity) {
        val records = activity.records
        var i = 0
        var j = 1
        while (j < records.size) {
            val rec1 = records[i]
            val rec1isNull = ActivityUtils.positionIsNull(rec1)
            val rec2 = records[j]
            val rec2isNull = ActivityUtils.positionIsNull(rec2)

            if (rec1isNull) {
                rec1.mark = ActivityUtils.MARK_DISABLED
                i++
            } else if (rec2isNull || speedIsTooHigh(rec1, rec2)) {
                rec2.mark = ActivityUtils.MARK_DISABLED
            } else {
                i = j
            }
            j++
        }
        activity.marked = true
        activityRepository.save(activity)
        log.info { "Saved marked up activity $activity" }
    }

    fun speedIsTooHigh(
        rec1: Record,
        rec2: Record,
    ): Boolean = ActivityUtils.calcSpeed(rec1, rec2) > appProperties.activityScheduler.maxSpeed
}
