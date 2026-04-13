package fit.man.app.controller;

import fit.man.app.service.ActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetTime;

@RequiredArgsConstructor
@Controller
public class UiController {
    private final ActivityService activityService;

    @GetMapping("/map")
    public String showPage(Model model) {
        var startTimeBegin = LocalDate.parse("2025-07-05")
                .atTime(LocalTime.MIDNIGHT)
                .atOffset(OffsetTime.now().getOffset()); // TODO
        var startTimeEnd = startTimeBegin.plusDays(1);
        var activityTrackPoints = activityService.getTrackPointsInRange(startTimeBegin, startTimeEnd);

        model.addAttribute("activityTrackPoints", activityTrackPoints);

        return "index";
    }
}
