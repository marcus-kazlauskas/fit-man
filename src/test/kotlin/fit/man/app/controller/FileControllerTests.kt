package fit.man.app.controller

import fit.man.app.service.ActivityService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.core.io.Resource
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(FileController::class)
class FileControllerTests {
    @Autowired
    lateinit var mockMvc: MockMvc

    @MockitoBean
    lateinit var activityService: ActivityService

    @Value("classpath:files/A24A93E6-A62E-466E-AF48-52F1ADD8684E.fit")
    lateinit var fitFile: Resource

    @Test
    fun shouldReturnActivity() {
        mockMvc
            .perform(
                post("/file/upload")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .content(fitFile.contentAsByteArray),
            ).andExpect(status().isOk())
    }
}
