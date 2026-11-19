package fit.man.app.mapper

import fit.man.app.api.model.ActivityResponse
import fit.man.app.api.model.ActivitySummary
import fit.man.app.api.model.TrackResponse
import fit.man.app.repository.entity.Activity
import fit.man.app.repository.entity.Event
import fit.man.app.repository.entity.Record
import fit.man.app.util.ActivityUtils
import java.time.format.DateTimeFormatter
import fit.man.app.api.model.Event as EventResponse
import fit.man.app.api.model.Record as RecordResponse

fun Activity.toResponse(): ActivityResponse =
    ActivityResponse(
        id = id,
        endTime = endTime!!,
        startTime = startTime!!,
        sport = sport!!,
        userName = userName!!,
        deviceName = deviceName!!,
        marked = marked,
        totalElapsedTime = totalElapsedTime?.toString(),
        totalTimerTime = totalTimerTime?.toString(),
        totalDistance = totalDistance,
        totalCalories = totalCalories?.toFloat(),
        totalAscent = totalAscent?.toFloat(),
        enhancedAvgSpeed = enhancedAvgSpeed,
        enhancedMaxSpeed = enhancedMaxSpeed,
        records = records.map { it.toResponse() },
        events = events.map { it.toResponse() },
    )

fun Record.toResponse(): RecordResponse =
    RecordResponse(
        id = id,
        mark = mark!!.toInt(),
        positionTime = positionTime?.let { DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(it) },
        positionLat = positionLat,
        positionLong = positionLong,
        distance = distance?.toDouble(),
        enhancedSpeed = enhancedSpeed?.toDouble(),
        enhancedAltitude = enhancedAltitude?.toDouble(),
    )

fun Event.toResponse(): EventResponse =
    EventResponse(
        id = id,
        eventTime = eventTime?.let { DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(it) },
        eventName = eventName,
        eventType = eventType,
    )

private fun Activity.trackMetrics(): Triple<Float?, Long?, Float?> {
    val currentAnalysis = analysis
    return if (currentAnalysis == null || !currentAnalysis.success) {
        Triple(totalDistance, totalTimerTime!!.seconds, enhancedAvgSpeed)
    } else {
        Triple(currentAnalysis.totalDistance, currentAnalysis.movingTime, currentAnalysis.averageSpeed)
    }
}

fun Activity.toTrackResponse(): TrackResponse {
    val (trackDistance, trackMovingTime, trackAvgSpeed) = trackMetrics()

    return TrackResponse(
        id = id,
        startTime = ActivityUtils.toLocalDateTimeString(startTime!!),
        totalElapsedTime = totalElapsedTime!!.seconds,
        totalDistance = trackDistance,
        movingTime = trackMovingTime,
        averageSpeed = trackAvgSpeed,
        points =
            records
                .filter { it.positionLat != null && it.positionLong != null && it.mark == ActivityUtils.MARK_DEFAULT }
                .map { listOf(it.positionLat!!, it.positionLong!!) },
    )
}

fun Activity.toSummary(): ActivitySummary {
    val (distance, movingTime, avgSpeed) = trackMetrics()

    return ActivitySummary(
        id = id,
        startTime = ActivityUtils.toLocalDateTimeString(startTime!!),
        totalElapsedTime = totalElapsedTime!!.seconds,
        totalDistance = distance,
        movingTime = movingTime,
        averageSpeed = avgSpeed,
    )
}
