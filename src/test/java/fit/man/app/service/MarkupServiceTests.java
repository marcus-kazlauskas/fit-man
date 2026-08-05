package fit.man.app.service;

import fit.man.app.config.AppProperties;
import fit.man.app.fixtures.ActivityFixtures;
import fit.man.app.mapper.ActivityMapperImpl;
import fit.man.app.repository.ActivityRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

@Import({
        MarkupService.class,
        ActivityService.class,
        ActivityMapperImpl.class
})
@EnableConfigurationProperties(AppProperties.class)
@SpringJUnitConfig
public class MarkupServiceTests {
    @Autowired
    private MarkupService markupService;

    @Autowired
    private ActivityService activityService;

    @MockitoBean
    private ActivityRepository activityRepository;

    @Test
    void shouldMarkActivity() {
        var activity = ActivityFixtures.createNewActivity();
        activity.setRecords(new ArrayList<>());
        activity.addRecord(ActivityFixtures.createRecordWithNullLat());
        activity.addRecord(ActivityFixtures.createRecordWithNullLong());
        activity.addRecord(ActivityFixtures.createRecord1());
        activity.addRecord(ActivityFixtures.createRecordWithFarPos());
        activity.addRecord(ActivityFixtures.createRecordWithNullLat());
        activity.addRecord(ActivityFixtures.createRecordWithNullLong());
        activity.addRecord(ActivityFixtures.createRecord2());
        activity.addRecord(ActivityFixtures.createRecord2());

        Mockito.when(activityRepository.findByMarkedFalse(
                any(PageRequest.class)
        )).thenReturn(List.of(activity));

        markupService.runMarkup();

        Mockito.when(activityRepository.findFirstByStartTimeBetweenOrderByStartTime(
                any(OffsetDateTime.class), any(OffsetDateTime.class)
        )).thenReturn(Optional.of(activity));

        var track = activityService.getTrackInRange(
                "2026-04-26T13:12:00", "2026-04-26T13:12:00"
        );

        assertThat(track).isNotNull();
        assertThat(track.getPoints()).isNotEmpty().hasSize(3);
    }
}
