package fit.man.app.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Controller
public class UiController {
    @GetMapping("/map")
    public String showPage(Model model) {
        return "index";
    }
}
