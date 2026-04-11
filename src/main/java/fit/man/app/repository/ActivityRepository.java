package fit.man.app.repository;

import fit.man.app.repository.entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {
    boolean existsByStartTime(OffsetDateTime startTime);

    Activity findByStartTime(OffsetDateTime startTime);
}
