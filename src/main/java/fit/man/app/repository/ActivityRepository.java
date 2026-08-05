package fit.man.app.repository;

import fit.man.app.repository.entity.Activity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {
    boolean existsByStartTime(OffsetDateTime startTime);

    Optional<Activity> findFirstByStartTimeBetweenOrderByStartTime(
            OffsetDateTime startTimeBegin,
            OffsetDateTime startTimeEnd
    );

    @Query("""
            SELECT a FROM Activity a
            LEFT JOIN FETCH a.analysis an
            WHERE a.marked = false
            """)
    List<Activity> findByMarkedFalse(Pageable pageable);

    @Query("""
            SELECT a FROM Activity a
            LEFT JOIN FETCH a.analysis an
            WHERE a.marked = true
            AND an IS NULL
            """)
    List<Activity> findByMarkedTrueAndAnalysisIsNull(Pageable pageable);
}
