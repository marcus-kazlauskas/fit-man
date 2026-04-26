package fit.man.app.controller;

import fit.man.app.service.ActivityService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FileController.class)
public class FileControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ActivityService activityService;

    @Value("classpath:files/A24A93E6-A62E-466E-AF48-52F1ADD8684E.fit")
    private Resource fitFile;

    @Test
    void shouldReturnActivity() throws Exception {
        mockMvc.perform(post("/file/upload")
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .content(fitFile.getContentAsByteArray())
                ).andExpect(status().isOk());
    }
}
