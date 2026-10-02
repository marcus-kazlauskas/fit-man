package fit.man.app.controller

import fit.man.app.api.model.TrackResponse
import fit.man.app.service.ActivityService
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(TrackController::class)
class TrackControllerTests {
    @Autowired
    lateinit var mockMvc: MockMvc

    @MockitoBean
    lateinit var activityService: ActivityService

    @Test
    fun shouldReturnTrackPoints() {
        whenever(activityService.getTrackInRange(anyString(), anyString()))
            .thenReturn(TrackResponse("2026-04-01T13:12:00.000", null, null, null, null, null))

        mockMvc.perform(
            get("/track/points")
                .param("startTimeBegin", "2025-07-05T02:00:00")
                .param("startTimeEnd", "2025-07-06T02:00:00")
        ).andExpect(status().isOk())
    }
}
