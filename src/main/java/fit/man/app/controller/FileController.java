package fit.man.app.controller;

import fit.man.app.advice.exception.FitFileException;
import fit.man.app.api.FileApi;
import fit.man.app.api.model.ActivityResponse;
import fit.man.app.service.ActivityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Controller
public class FileController implements FileApi {
    private final ActivityService activityService;

    @Override
    public ResponseEntity<ActivityResponse> postFileUpload(Resource body) {
        try (var is = body.getInputStream()) {
            return ResponseEntity.ok(activityService.loadNewActivity(is));
        } catch (IOException e) {
            log.atError().log(e.getMessage(), e);
            throw new FitFileException(e.getMessage(), e);
        }
    }
}
