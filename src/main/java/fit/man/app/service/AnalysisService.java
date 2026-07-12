package fit.man.app.service;

import fit.man.app.config.AppProperties;
import fit.man.app.repository.AnalysisRepository;
import fit.man.app.repository.entity.Activity;
import fit.man.app.repository.entity.Analysis;
import fit.man.app.repository.entity.Event;
import fit.man.app.repository.entity.Record;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Service
public class AnalysisService {
    private final ActivityService activityService;
    private final AnalysisRepository analysisRepository;
    private final AppProperties appProperties;

    @Qualifier("analysisExecutor")
    private final Executor analysisExecutor;

    public void runAnalysis() {
        var activities = activityService.findActivitiesForAnalysis();
        for (var activity : activities) {
            analyzeAnSave(activity);
        }
    }

    private void analyzeAnSave(Activity activity) {
        var analysis = new Analysis();
        analysis.setActivity(activity);

        var records = activity.getRecords();
        var events = activity.getEvents();

        CompletableFuture<Float> futureTotalDistance = CompletableFuture.supplyAsync(
                () -> calcTotalDistance(records), analysisExecutor
        ).orTimeout(appProperties.activityScheduler().timeout(), TimeUnit.SECONDS);

        CompletableFuture<Long> futureMovingTime = CompletableFuture.supplyAsync(
                () -> calcMovingTime(records, events), analysisExecutor
        ).orTimeout(appProperties.activityScheduler().timeout(), TimeUnit.SECONDS);

        try {
            CompletableFuture.allOf(futureTotalDistance, futureMovingTime).join();

            var totalDistance = futureTotalDistance.get();
            var movingTime = futureMovingTime.get();

            analysis.setTotalDistance(totalDistance);
            analysis.setMovingTime(movingTime);
            analysis.setAverageSpeed(totalDistance / movingTime);
            analysis.setSuccess(true);
        } catch (Exception e) {
            analysis.setSuccess(false);
            log.atWarn().log("Exception occurred during analysis: ", e);
        }

        analysisRepository.save(analysis);
        log.atInfo().log("Saved analysis {}", analysis);
    }

    private float calcTotalDistance(List<Record> records) {
        if (records.isEmpty()) {
            return 0f; // TODO
        } else {
            return 1f;
        }
    }

    private long calcMovingTime(List<Record> records, List<Event> events) {
        if (records.isEmpty() && events.isEmpty()) {
            return 0; // TODO
        } else {
            return 1;
        }
    }
}
