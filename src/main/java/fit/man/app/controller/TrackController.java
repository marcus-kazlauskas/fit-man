package fit.man.app.controller;

import fit.man.app.api.TrackApi;
import fit.man.app.api.model.TrackResponse;
import fit.man.app.service.ActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

@RequiredArgsConstructor
@Controller
public class TrackController implements TrackApi {
    private final ActivityService activityService;

    @Override
    public ResponseEntity<TrackResponse> getTrackPoints(String startTimeBegin, String startTimeEnd) {
        return ResponseEntity.ok(
                activityService.getTrackInRange(startTimeBegin, startTimeEnd)
        );
    }
}
