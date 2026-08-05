package fit.man.app.service;

import fit.man.app.config.AppProperties;
import fit.man.app.fixtures.ActivityFixtures;
import fit.man.app.mapper.ActivityMapperImpl;
import fit.man.app.repository.ActivityRepository;
import fit.man.app.repository.AnalysisRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

@Import({
        AnalysisService.class,
        ActivityService.class,
        ActivityMapperImpl.class
})
@EnableConfigurationProperties(AppProperties.class)
@SpringJUnitConfig
public class AnalysisServiceTests {
    @Autowired
    private AnalysisService analysisService;

    @Autowired
    private ActivityService activityService;

    @MockitoBean
    private ActivityRepository activityRepository;

    @MockitoBean
    private AnalysisRepository analysisRepository;

    @Test
    void shouldAnalyzeActivity() {
        var activity = ActivityFixtures.createNewActivity();

        Mockito.when(activityRepository.findByMarkedTrueAndAnalysisIsNull(
                any(PageRequest.class)
        )).thenReturn(List.of(activity));

        analysisService.runAnalysis();
    }

    @Test
    void shouldCalcZeroDistance() {
        var activity = ActivityFixtures.createNewActivity();
        activity.setRecords(List.of());
        activity.setEvents(List.of());

        var totalDistance = AnalysisService.calcTotalDistance(activity.getRecords());
        var movingTime = AnalysisService.calcMovingTime(activity.getRecords(), activity.getEvents());

        assertThat(Math.round(totalDistance)).isEqualTo(0);
        assertThat(Math.round(movingTime)).isEqualTo(0);
    }

    @Test
    void shouldCalcTotalDistance() {
        var activity = ActivityFixtures.createNewActivity();
        activity.setRecords(new ArrayList<>());
        activity.addRecord(ActivityFixtures.createRecordWithMarkDisabled());
        activity.addRecord(ActivityFixtures.createRecord1());
        activity.addRecord(ActivityFixtures.createRecordWithMarkDisabled());
        activity.addRecord(ActivityFixtures.createRecord2());

        var totalDistance = AnalysisService.calcTotalDistance(activity.getRecords());

        assertThat(Math.round(totalDistance)).isEqualTo(12776);
    }

    @Test
    void shouldCalcMovingTime() {
        var activity = ActivityFixtures.createNewActivity();
        activity.setRecords(new ArrayList<>());
        activity.addRecord(ActivityFixtures.createRecordWithMarkDisabled());
        activity.addRecord(ActivityFixtures.createRecord1());
        activity.addRecord(ActivityFixtures.createRecordWithMarkDisabled());
        activity.addRecord(ActivityFixtures.createRecord2());
        activity.addRecord(ActivityFixtures.createRecord3());
        activity.addRecord(ActivityFixtures.createRecord4());
        activity.setEvents(new ArrayList<>());
        activity.addEvent(ActivityFixtures.createEvent1());
        activity.addEvent(ActivityFixtures.createDisabledEvent());
        activity.addEvent(ActivityFixtures.createEvent2());
        activity.addEvent(ActivityFixtures.createEvent3());
        activity.addEvent(ActivityFixtures.createEvent4());

        var movingTime = AnalysisService.calcMovingTime(activity.getRecords(), activity.getEvents());

        assertThat(Math.round(movingTime)).isEqualTo(1380);
    }
}
