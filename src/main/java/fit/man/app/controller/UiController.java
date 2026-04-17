package fit.man.app.controller;

import fit.man.app.service.ActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Controller
public class UiController {
    private final ActivityService activityService;

    @GetMapping("/map")
    public String showPage(Model model) {
        return "index";
    }
}
