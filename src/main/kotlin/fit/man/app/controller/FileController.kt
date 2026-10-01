package fit.man.app.controller

import fit.man.app.advice.exception.FitFileException
import fit.man.app.api.FileApi
import fit.man.app.api.model.ActivityResponse
import fit.man.app.service.ActivityService
import org.slf4j.LoggerFactory
import org.springframework.core.io.Resource
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import java.io.IOException

@Controller
class FileController(
    private val activityService: ActivityService
) : FileApi {
    override fun postFileUpload(body: Resource): ResponseEntity<ActivityResponse> {
        return try {
            body.inputStream.use { ResponseEntity.ok(activityService.loadNewActivity(it)) }
        } catch (e: IOException) {
            log.atError().log(e.message, e)
            throw FitFileException(e.message.orEmpty(), e)
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(FileController::class.java)
    }
}
