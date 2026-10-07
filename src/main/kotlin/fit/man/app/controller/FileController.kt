package fit.man.app.controller

import fit.man.app.advice.exception.FitFileException
import fit.man.app.api.FileApi
import fit.man.app.api.model.ActivityResponse
import fit.man.app.service.ActivityService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.core.io.Resource
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import java.io.IOException

private val log = KotlinLogging.logger {}

@Controller
class FileController(
    private val activityService: ActivityService,
) : FileApi {
    override fun postFileUpload(body: Resource): ResponseEntity<ActivityResponse> =
        try {
            body.inputStream.use { ResponseEntity.ok(activityService.loadNewActivity(it)) }
        } catch (e: IOException) {
            log.error(e) { e.message }
            throw FitFileException(e.message.orEmpty(), e)
        }
}
