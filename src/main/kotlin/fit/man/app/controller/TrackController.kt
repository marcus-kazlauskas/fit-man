package fit.man.app.controller

import fit.man.app.api.TrackApi
import fit.man.app.api.model.TrackResponse
import fit.man.app.service.ActivityService
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller

@Controller
class TrackController(
    private val activityService: ActivityService
) : TrackApi {
    override fun getTrackPoints(startTimeBegin: String, startTimeEnd: String): ResponseEntity<TrackResponse> {
        return ResponseEntity.ok(activityService.getTrackInRange(startTimeBegin, startTimeEnd))
    }
}
