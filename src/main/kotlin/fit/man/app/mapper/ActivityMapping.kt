package fit.man.app.mapper

import fit.man.app.api.model.ActivityResponse
import fit.man.app.api.model.Event as EventResponse
import fit.man.app.api.model.Record as RecordResponse
import fit.man.app.api.model.TrackResponse
import fit.man.app.repository.entity.Activity
import fit.man.app.repository.entity.Event
import fit.man.app.repository.entity.Record
import fit.man.app.util.ActivityUtils
import java.time.format.DateTimeFormatter

fun Activity.toResponse(): ActivityResponse {
    val response = ActivityResponse()
    response.id = id
    endTime?.let { response.endTime = it }
    startTime?.let { response.startTime = it }
    sport?.let { response.sport = it }
    totalElapsedTime?.let { response.totalElapsedTime = it.toString() }
    totalTimerTime?.let { response.totalTimerTime = it.toString() }
    response.totalDistance = totalDistance
    totalCalories?.let { response.totalCalories = it.toFloat() }
    totalAscent?.let { response.totalAscent = it.toFloat() }
    response.enhancedAvgSpeed = enhancedAvgSpeed
    response.enhancedMaxSpeed = enhancedMaxSpeed
    userName?.let { response.userName = it }
    deviceName?.let { response.deviceName = it }
    response.marked = marked
    response.records = records.map { it.toResponse() }
    response.events = events.map { it.toResponse() }
    return response
}

fun Record.toResponse(): RecordResponse {
    val response = RecordResponse()
    response.id = id
    positionTime?.let { response.positionTime = DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(it) }
    response.positionLat = positionLat
    response.positionLong = positionLong
    distance?.let { response.distance = it.toDouble() }
    enhancedSpeed?.let { response.enhancedSpeed = it.toDouble() }
    enhancedAltitude?.let { response.enhancedAltitude = it.toDouble() }
    mark?.let { response.mark = it.toInt() }
    return response
}

fun Event.toResponse(): EventResponse {
    val response = EventResponse()
    response.id = id
    eventTime?.let { response.eventTime = DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(it) }
    response.eventName = eventName
    response.eventType = eventType
    return response
}

fun Activity?.toTrackResponse(): TrackResponse {
    val trackResponse = TrackResponse()
    if (this == null) {
        return trackResponse
    }

    trackResponse.startTime = ActivityUtils.toLocalDateTimeString(startTime!!)
    trackResponse.totalElapsedTime = totalElapsedTime!!.seconds

    val currentAnalysis = analysis
    if (currentAnalysis == null || !currentAnalysis.success) {
        trackResponse.totalDistance = totalDistance
        trackResponse.movingTime = totalTimerTime!!.seconds
        trackResponse.averageSpeed = enhancedAvgSpeed
    } else {
        trackResponse.totalDistance = currentAnalysis.totalDistance
        trackResponse.movingTime = currentAnalysis.movingTime
        trackResponse.averageSpeed = currentAnalysis.averageSpeed
    }

    trackResponse.points = records
        .filter { it.positionLat != null && it.positionLong != null && it.mark == ActivityUtils.MARK_DEFAULT }
        .map { listOf(it.positionLat!!, it.positionLong!!) }

    return trackResponse
}
