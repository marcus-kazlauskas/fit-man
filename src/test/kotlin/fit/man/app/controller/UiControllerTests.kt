package fit.man.app.controller

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.view

@WebMvcTest(UiController::class)
class UiControllerTests {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun shouldReturnIndex() {
        mockMvc
            .perform(get("/map"))
            .andExpect(status().isOk())
            .andExpect(view().name("index"))
    }
}
