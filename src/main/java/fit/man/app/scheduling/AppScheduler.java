package fit.man.app.scheduling;

import fit.man.app.service.AnalysisService;
import fit.man.app.service.MarkupService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AppScheduler {
    private final MarkupService markupService;
    private final AnalysisService analysisService;

    @Scheduled(fixedRateString = "${fit-man.activity-scheduler.fixed-rate:PT1M}")
    public void runActivityMarkup() {
        markupService.runMarkup();
    }

    @Scheduled(
            fixedRateString = "${fit-man.activity-scheduler.fixed-rate:PT1M}",
            initialDelayString = "${fit-man.activity-scheduler.initial-delay:PT30S}"
    )
    public void runActivityAnalysis() {
        analysisService.runAnalysis();
    }
}
