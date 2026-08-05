package fit.man.app.service;

import fit.man.app.config.AppProperties;
import fit.man.app.repository.ActivityRepository;
import fit.man.app.repository.entity.Activity;
import fit.man.app.repository.entity.Record;
import fit.man.app.util.ActivityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class MarkupService {
    private final ActivityService activityService;
    private final ActivityRepository activityRepository;
    private final AppProperties appProperties;

    public void runMarkup() {
        var activities = activityService.findActivitiesForMarkup();
        for (var activity : activities) {
            markAndSave(activity);
        }
    }

    private void markAndSave(Activity activity) {
        var records = activity.getRecords();
        var i = 0;
        var j = 1;
        while (j < records.size()) {
            var rec1 = records.get(i);
            var rec1isNull = ActivityUtils.positionIsNull(rec1);
            var rec2 = records.get(j);
            var rec2isNull = ActivityUtils.positionIsNull(rec2);

            if (rec1isNull) {
                rec1.setMark(ActivityUtils.MARK_DISABLED);
                i++;
            } else if (rec2isNull || speedIsTooHigh(rec1, rec2)) {
                rec2.setMark(ActivityUtils.MARK_DISABLED);
            } else {
                i = j;
            }
            j++;
        }
        activity.setMarked(true);
        activityRepository.save(activity);
        log.atInfo().log("Saved marked up activity {}", activity);
    }

    public boolean speedIsTooHigh(fit.man.app.repository.entity.Record rec1, Record rec2) {
        return ActivityUtils.calcSpeed(rec1, rec2) > appProperties.activityScheduler().maxSpeed();
    }
}
