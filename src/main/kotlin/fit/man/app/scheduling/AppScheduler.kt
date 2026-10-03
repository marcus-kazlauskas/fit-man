package fit.man.app.scheduling

import fit.man.app.service.AnalysisService
import fit.man.app.service.MarkupService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class AppScheduler(
    private val markupService: MarkupService,
    private val analysisService: AnalysisService,
) {
    @Scheduled(fixedRateString = "\${fit-man.activity-scheduler.fixed-rate:PT1M}")
    fun runActivityMarkup() {
        markupService.runMarkup()
    }

    @Scheduled(
        fixedRateString = "\${fit-man.activity-scheduler.fixed-rate:PT1M}",
        initialDelayString = "\${fit-man.activity-scheduler.initial-delay:PT30S}",
    )
    fun runActivityAnalysis() {
        analysisService.runAnalysis()
    }
}
