package fit.man.app.scheduling;

import fit.man.app.service.ActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ActivityScheduler {
    private final ActivityService activityService;

    @Scheduled(fixedRateString = "${fit-man.activity-markup-scheduler.fixed-rate:PT1M}")
    public void runTrackMarkup() {
        activityService.markActivities();
    }
}
