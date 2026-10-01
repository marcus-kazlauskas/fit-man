package fit.man.app.controller

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class UiController {
    @GetMapping("/map")
    fun showPage(model: Model): String {
        return "index"
    }
}
