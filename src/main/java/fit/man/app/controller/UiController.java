package fit.man.app.controller;

import fit.man.app.service.ActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.OffsetDateTime;

@RequiredArgsConstructor
@Controller
public class UiController {
    private final ActivityService activityService;

    @GetMapping("/map")
    public String showPage(Model model) {
        var startTime = OffsetDateTime.parse("2025-07-05T02:59:50+03:00"); // TODO
        var activityTrackPoints = activityService.getActivityTrackPoints(startTime);

        model.addAttribute("activityTrackPoints", activityTrackPoints);

        return "index";
    }
}
