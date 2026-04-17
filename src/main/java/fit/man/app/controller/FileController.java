package fit.man.app.controller;

import fit.man.app.api.FileApi;
import fit.man.app.api.model.ActivityResponse;
import fit.man.app.service.ActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

@RequiredArgsConstructor
@Controller
public class FileController implements FileApi {
    private final ActivityService activityService;

    @Override
    public ResponseEntity<ActivityResponse> postFileUpload(Resource body) {
        return ResponseEntity.ok(activityService.loadNewActivity(body));
    }
}
