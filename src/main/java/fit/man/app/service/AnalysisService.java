package fit.man.app.service;

import com.garmin.fit.EventType;
import fit.man.app.config.AppProperties;
import fit.man.app.repository.AnalysisRepository;
import fit.man.app.repository.entity.Activity;
import fit.man.app.repository.entity.Analysis;
import fit.man.app.repository.entity.Event;
import fit.man.app.repository.entity.Record;
import fit.man.app.util.ActivityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
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

        CompletableFuture<Double> futureTotalDistance = CompletableFuture.supplyAsync(
                () -> calcTotalDistance(records), analysisExecutor
        ).orTimeout(appProperties.activityScheduler().timeout(), TimeUnit.SECONDS);

        CompletableFuture<Double> futureMovingTime = CompletableFuture.supplyAsync(
                () -> calcMovingTime(records, events), analysisExecutor
        ).orTimeout(appProperties.activityScheduler().timeout(), TimeUnit.SECONDS);

        try {
            CompletableFuture.allOf(futureTotalDistance, futureMovingTime).join();

            var totalDistance = futureTotalDistance.join();
            var movingTime = futureMovingTime.join();

            analysis.setTotalDistance(totalDistance.floatValue());
            analysis.setMovingTime(movingTime.longValue());
            analysis.setAverageSpeed((float) (totalDistance / movingTime * ActivityUtils.KM_PER_HOUR));
            analysis.setSuccess(true);
        } catch (RuntimeException e) {
            analysis.setSuccess(false);
            log.atWarn().log("Exception occurred during analysis: ", e);
        }

        analysisRepository.save(analysis);
        log.atInfo().log("Saved analysis {}", analysis);
    }

    private double calcTotalDistance(List<Record> records) {
        if (records.isEmpty()) {
            return 0f;
        }

        double totalDistance = 0;
        var i = 0;
        var j = 1;
        while (j < records.size()) {
            var record1 = records.get(i);
            var record2 = records.get(j);
            if (record1.getMark().equals(ActivityUtils.MARK_DISABLED)) {
                i++;
            } else if (record2.getMark().equals(ActivityUtils.MARK_DEFAULT)) {
                totalDistance += ActivityUtils.calcDistance(
                        record1.getPositionLat(),
                        record1.getPositionLong(),
                        record2.getPositionLat(),
                        record2.getPositionLong()
                );
                i = j;
            }
            j++;
        }
        return totalDistance;
    }

    private double calcMovingTime(List<Record> records, List<Event> events) {
        if (records.isEmpty() || events.isEmpty()) {
            return 0;
        }

        double trackTime = 0;
        var i = 0;
        var j = 1;

        var validEvents = new ArrayList<Event>();
        var k = 0;

        while (j < records.size()) {
            var record1 = records.get(i);
            var record2 = records.get(j);
            if (record1.getMark().equals(ActivityUtils.MARK_DISABLED)) {
                i++;
            } else if (record2.getMark().equals(ActivityUtils.MARK_DEFAULT)) {
                trackTime += Duration.between(
                        record1.getPositionTime(),
                        record2.getPositionTime()
                ).toMillis() / (double) ActivityUtils.MILLIS;
                i = j;

                if (k < events.size()) {
                    var event = events.get(k);
                    var eventTime = event.getEventTime().truncatedTo(ChronoUnit.SECONDS);
                    var validTime = record1.getPositionTime().truncatedTo(ChronoUnit.SECONDS);
                    if (eventTime.equals(validTime)) {
                        validEvents.add(event);
                        k++;
                    } else if (eventTime.isBefore(validTime)) {
                        k++;
                    }
                }
            }
            j++;
        }
        log.atInfo().log("Collected valid {} timer events {}", validEvents.size(), validEvents);

        double idlingTime = 0;
        k = 0;
        var l = 1;
        while (l < validEvents.size()) {
            var event1 = validEvents.get(k);
            var event2 = validEvents.get(l);
            if (event1.getEventType().equals(EventType.START.name())) {
                k++;
                l++;
            } else if (event2.getEventType().equals(EventType.STOP.name())
                    || event2.getEventType().equals(EventType.STOP_ALL.name())) {
                l++;
            } else {
                idlingTime += Duration.between(
                        event1.getEventTime(),
                        event2.getEventTime()
                ).toMillis() / (double) ActivityUtils.MILLIS;
                k = l + 1;
                l = k + 1;
            }
        }
        var movingTime = trackTime - idlingTime;
        log.atInfo().log("Result: trackTime[{}] - idlingTime[{}] = movingTime[{}]",
                trackTime, idlingTime, movingTime);

        return movingTime;
    }
}
