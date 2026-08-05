package fit.man.app.service;

import fit.man.app.advice.exception.ActivityNotFoundException;
import fit.man.app.advice.exception.FitFileException;
import fit.man.app.api.model.ActivityResponse;
import fit.man.app.config.AppProperties;
import fit.man.app.fixtures.ActivityFixtures;
import fit.man.app.mapper.ActivityMapperImpl;
import fit.man.app.repository.ActivityRepository;
import fit.man.app.repository.entity.Activity;
import fit.man.app.util.ActivityUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;

@Import({
        ActivityService.class,
        ActivityMapperImpl.class
})
@EnableConfigurationProperties(AppProperties.class)
@SpringJUnitConfig
public class ActivityServiceTests {
    @Autowired
    private ActivityService activityService;

    @MockitoBean
    private ActivityRepository activityRepository;

    @Value("classpath:files/A24A93E6-A62E-466E-AF48-52F1ADD8684E.fit")
    private Resource fitFile;

    @Value("classpath:files/56CFD91A-E05E-43AA-B878-BDC089258240.png")
    private Resource pngFile;

    @Test
    void shouldReadFitFile() throws Exception {
        Activity activity = activityService.readFitFile(fitFile.getInputStream());

        assertThat(activity).isNotNull();
        Assertions.assertAll(
                () -> assertEquals(OffsetDateTime.parse("2025-07-05T14:07:18.000+03:00"), activity.getEndTime()),
                () -> assertEquals(OffsetDateTime.parse("2025-07-05T02:59:50.000+03:00"), activity.getStartTime()),
                () -> assertEquals("CYCLING", activity.getSport()),
                () -> assertEquals(Duration.parse("PT11H07M27.784S"), activity.getTotalElapsedTime()),
                () -> assertEquals(Duration.parse("PT03H30M04.197S"), activity.getTotalTimerTime()),
                () -> assertEquals(157846.22F, activity.getTotalDistance()),
                () -> assertEquals(5744, activity.getTotalCalories()),
                () -> assertEquals(239, activity.getTotalAscent()),
                () -> assertEquals(12.523F, activity.getEnhancedAvgSpeed()),
                () -> assertEquals(23.842F, activity.getEnhancedMaxSpeed()),
                () -> assertEquals("Mikhail Kozlov", activity.getUserName()),
                () -> assertEquals("Cannondale App", activity.getDeviceName()),
                () -> assertFalse(activity.isMarked())
        );
        assertThat(activity.getRecords()).isNotEmpty();
        var record = activity.getRecords().getFirst();
        Assertions.assertAll(
                () -> assertEquals(LocalDateTime.parse("2025-07-04T23:59:50.000"), record.getPositionTime()),
                () -> assertEquals(ActivityUtils.MARK_DEFAULT, record.getMark())
        );
    }

    @Test
    void shouldThrowExceptionWhenReadPngFile() {
        assertThatThrownBy(() -> activityService.readFitFile(pngFile.getInputStream()))
                .isInstanceOf(FitFileException.class);
    }

    @Test
    void shouldLoadNewActivity() throws Exception {
        Mockito.when(activityRepository.save(any(Activity.class)))
                .thenReturn(ActivityFixtures.createNewActivity());

        ActivityResponse response = activityService.loadNewActivity(fitFile.getInputStream());

        assertThat(response).isNotNull();
        assertThat(response.getStartTime()).isEqualTo(ActivityFixtures.START_TIME);
        assertThat(response.getRecords()).isNotEmpty();
    }

    @Test
    void shouldThrowExceptionWhenActivityExists() {
        Mockito.when(activityRepository.existsByStartTime(any(OffsetDateTime.class)))
                .thenReturn(true);

        assertThatThrownBy(() -> activityService.checkNotExistsAndSave(ActivityFixtures.createNewActivity()))
                .isInstanceOf(FitFileException.class);
    }

    @Test
    void shouldGetTrackInRange() {
        var activity = ActivityFixtures.createNewActivity();
        activity.addRecord(ActivityFixtures.createRecordWithNullLat());
        activity.addRecord(ActivityFixtures.createRecordWithNullLong());
        activity.addRecord(ActivityFixtures.createRecordWithMarkDisabled());

        Mockito.when(activityRepository.findFirstByStartTimeBetweenOrderByStartTime(
                any(OffsetDateTime.class), any(OffsetDateTime.class)
        )).thenReturn(Optional.of(activity));

        var track = activityService.getTrackInRange(
                "2026-04-26T13:12:00", "2026-04-26T13:12:00"
        );

        assertThat(track).isNotNull();
        Assertions.assertAll(
                () -> assertEquals(1, track.getPoints().size()),
                () -> assertEquals("2026-04-23T13:12", track.getStartTime()),
                () -> assertEquals(86400, track.getTotalElapsedTime()),
                () -> assertEquals(13.12F, track.getTotalDistance()),
                () -> assertEquals(47520, track.getMovingTime()),
                () -> assertEquals(4F, track.getAverageSpeed())
        );
    }

    @Test
    void shouldGetTrackWithSuccessfulAnalysis() {
        var activity = ActivityFixtures.createNewActivity();
        var analysis = ActivityFixtures.createAnalysis();
        activity.setAnalysis(analysis);

        Mockito.when(activityRepository.findFirstByStartTimeBetweenOrderByStartTime(
                any(OffsetDateTime.class), any(OffsetDateTime.class)
        )).thenReturn(Optional.of(activity));

        var track = activityService.getTrackInRange(
                "2026-04-26T13:12:00", "2026-04-26T13:12:00"
        );

        assertThat(track).isNotNull();
        Assertions.assertAll(
                () -> assertEquals(1, track.getPoints().size()),
                () -> assertEquals("2026-04-23T13:12", track.getStartTime()),
                () -> assertEquals(86400, track.getTotalElapsedTime()),
                () -> assertEquals(13000F, track.getTotalDistance()),
                () -> assertEquals(43200L, track.getMovingTime()),
                () -> assertEquals(1.08F, track.getAverageSpeed())
        );
    }

    @Test
    void shouldGetTrackWithUnsuccessfulAnalysis() {
        var activity = ActivityFixtures.createNewActivity();
        var analysis = ActivityFixtures.createUnsuccessfulAnalysis();
        activity.setAnalysis(analysis);

        Mockito.when(activityRepository.findFirstByStartTimeBetweenOrderByStartTime(
                any(OffsetDateTime.class), any(OffsetDateTime.class)
        )).thenReturn(Optional.of(activity));

        var track = activityService.getTrackInRange(
                "2026-04-26T13:12:00", "2026-04-26T13:12:00"
        );

        assertThat(track).isNotNull();
        Assertions.assertAll(
                () -> assertEquals(1, track.getPoints().size()),
                () -> assertEquals("2026-04-23T13:12", track.getStartTime()),
                () -> assertEquals(86400, track.getTotalElapsedTime()),
                () -> assertEquals(13.12F, track.getTotalDistance()),
                () -> assertEquals(47520, track.getMovingTime()),
                () -> assertEquals(4F, track.getAverageSpeed())
        );
    }

    @Test
    void shouldThrowExceptionWhenActivityNotFound() {
        Mockito.when(activityRepository.findFirstByStartTimeBetweenOrderByStartTime(
                any(OffsetDateTime.class), any(OffsetDateTime.class)
        )).thenReturn(Optional.empty());

        assertThatThrownBy(() -> activityService.getTrackInRange(
                "2026-04-26T13:12:00", "2026-04-26T13:12:00"
        )).isInstanceOf(ActivityNotFoundException.class);
    }
}
