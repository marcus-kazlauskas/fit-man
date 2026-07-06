package fit.man.app.advice;

import fit.man.app.advice.exception.ActivityNotFoundException;
import fit.man.app.advice.exception.FitFileException;
import fit.man.app.service.ActivityService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.io.InputStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(GlobalExceptionHandler.class)
@WebMvcTest
public class GlobalExceptionHandlerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ActivityService activityService;

    @Value("classpath:files/56CFD91A-E05E-43AA-B878-BDC089258240.png")
    private Resource pngFile;

    @Test
    void shouldHandleFitFileException() throws Exception {
        Mockito.when(activityService.loadNewActivity(any(InputStream.class)))
                        .thenThrow(new FitFileException(""));

        mockMvc.perform(post("/file/upload")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .content(pngFile.getContentAsByteArray())
        ).andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    void shouldHandleActivityNotFoundException() throws Exception {
        Mockito.when(activityService.getTrackInRange(anyString(), anyString()))
                .thenThrow(new ActivityNotFoundException(""));

        mockMvc.perform(get("/track/points")
                .param("startTimeBegin", "2025-07-05T02:00:00")
                .param("startTimeEnd", "2025-07-06T02:00:00")
        ).andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()));
    }
}
