package fit.man.app.repository;


import fit.man.app.fixtures.ActivityFixtures;
import fit.man.app.repository.entity.Activity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class ActivityRepositoryTests {
    @Autowired
    private ActivityRepository activityRepository;

    private Activity activity;

    @BeforeEach
    void setUp() {
        activity = ActivityFixtures.createNewActivity();
    }

    @Test
    void shouldSaveActivity() {
        var savedActivity = activityRepository.save(activity);

        assertThat(savedActivity.getId()).isNotNegative();
        assertThat(savedActivity.getRecords()).isNotNull();
    }

    @Test
    void shouldCheckActivityExists() {
        activityRepository.save(activity);
        var exists = activityRepository.existsByStartTime(ActivityFixtures.START_TIME);

        assertThat(exists).isEqualTo(true);
    }

    @Test
    void shouldFindActivityInRange() {
        activityRepository.save(activity);
        var foundActivity = activityRepository.findByStartTimeBetweenOrderByStartTime(
                ActivityFixtures.START_TIME.minusMinutes(1),
                ActivityFixtures.START_TIME.plusMinutes(1)
        );

        assertThat(foundActivity).isNotNull();
    }
}
