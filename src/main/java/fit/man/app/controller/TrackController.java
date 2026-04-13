package fit.man.app.controller;

import fit.man.app.api.TrackApi;
import fit.man.app.service.ActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.time.OffsetDateTime;
import java.util.List;

@RequiredArgsConstructor
@Controller
public class TrackController implements TrackApi {
    private final ActivityService activityService;

    @Override
    public ResponseEntity<List<List<Double>>> getTrackPoints(OffsetDateTime startTimeBegin, OffsetDateTime startTimeEnd) {
        return ResponseEntity.ok(
                activityService.getTrackPointsInRange(startTimeBegin, startTimeEnd)
        );
    }
}
