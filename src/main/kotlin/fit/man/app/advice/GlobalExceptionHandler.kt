package fit.man.app.advice

import fit.man.app.advice.exception.ActivityNotFoundException
import fit.man.app.advice.exception.FitFileException
import fit.man.app.api.model.ErrorResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(FitFileException::class)
    fun handleFitFile(e: FitFileException): ResponseEntity<ErrorResponse> {
        val err = ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.message.orEmpty())
        return ResponseEntity(err, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(ActivityNotFoundException::class)
    fun handleActivityNotFound(e: ActivityNotFoundException): ResponseEntity<ErrorResponse> {
        val err = ErrorResponse(HttpStatus.NOT_FOUND.value(), e.message.orEmpty())
        return ResponseEntity(err, HttpStatus.NOT_FOUND)
    }
}
