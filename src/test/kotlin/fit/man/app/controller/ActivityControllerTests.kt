package fit.man.app.controller

import fit.man.app.api.model.ActivityListResponse
import fit.man.app.service.ActivityService
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(ActivityController::class)
class ActivityControllerTests {
    @Autowired
    lateinit var mockMvc: MockMvc

    @MockitoBean
    lateinit var activityService: ActivityService

    @Test
    fun shouldReturnActivityList() {
        whenever(activityService.getActivityList(any(), any(), anyOrNull(), anyOrNull()))
            .thenReturn(ActivityListResponse(items = emptyList(), page = 0, propertySize = 10, totalElements = 0, totalPages = 0))

        mockMvc
            .perform(
                get("/activity/list")
                    .param("page", "0")
                    .param("size", "10"),
            ).andExpect(status().isOk())
    }
}
