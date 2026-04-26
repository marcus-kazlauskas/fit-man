package fit.man.app.controller;

import fit.man.app.api.model.TrackResponse;
import fit.man.app.service.ActivityService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TrackController.class)
public class TrackControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ActivityService activityService;

    @Test
    void shouldReturnTrackPoints() throws Exception {
        Mockito.when(activityService.getTrackInRange(anyString(), anyString()))
                        .thenReturn(new TrackResponse());

        mockMvc.perform(get("/track/points")
                        .param("startTimeBegin", "2025-07-05T02:00:00")
                        .param("startTimeEnd", "2025-07-06T02:00:00")
                ).andExpect(status().isOk());
    }
}
