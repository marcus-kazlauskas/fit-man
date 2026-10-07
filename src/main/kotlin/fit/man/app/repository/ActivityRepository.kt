package fit.man.app.repository

import fit.man.app.repository.entity.Activity
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.OffsetDateTime
import java.util.Optional

@Repository
interface ActivityRepository : JpaRepository<Activity, Long> {
    fun existsByStartTime(startTime: OffsetDateTime): Boolean

    fun findFirstByStartTimeBetweenOrderByStartTime(
        startTimeBegin: OffsetDateTime,
        startTimeEnd: OffsetDateTime,
    ): Optional<Activity>

    @Query(
        """
        SELECT a FROM Activity a
        LEFT JOIN FETCH a.analysis an
        WHERE a.marked = false
        """,
    )
    fun findByMarkedFalse(pageable: Pageable): List<Activity>

    @Query(
        """
        SELECT a FROM Activity a
        LEFT JOIN FETCH a.analysis an
        WHERE a.marked = true
        AND an IS NULL
        """,
    )
    fun findByMarkedTrueAndAnalysisIsNull(pageable: Pageable): List<Activity>
}
