package fit.man.app.controller;

import fit.man.app.api.TrackApi;
import fit.man.app.service.ActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.time.OffsetTime;
import java.util.List;

@RequiredArgsConstructor
@Controller
public class TrackController implements TrackApi {
    private final ActivityService activityService;

    @Override
    public ResponseEntity<List<List<Double>>> getTrackPoints(String startTimeBegin, String startTimeEnd) {
        var start = LocalDateTime.parse(startTimeBegin)
                .atOffset(OffsetTime.now().getOffset());
        var end = LocalDateTime.parse(startTimeEnd)
                .atOffset(OffsetTime.now().getOffset());

        return ResponseEntity.ok(
                activityService.getTrackPointsInRange(start, end)
        );
    }
}
