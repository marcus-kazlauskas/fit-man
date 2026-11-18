package fit.man.app.controller

import fit.man.app.api.ActivityApi
import fit.man.app.api.model.ActivityListResponse
import fit.man.app.service.ActivityService
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller

@Controller
class ActivityController(
    private val activityService: ActivityService,
) : ActivityApi {
    override fun getActivityList(
        page: Int,
        size: Int,
    ): ResponseEntity<ActivityListResponse> = ResponseEntity.ok(activityService.getActivityList(page, size))
}
