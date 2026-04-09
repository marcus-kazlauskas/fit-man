package fit.man.app.repository.entity;

import com.garmin.fit.Sport;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
@Entity
@Table(name = "activity")
public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private long id;

    @Column(name = "end_time", nullable = false)
    private OffsetDateTime endTime = OffsetDateTime.now();

    @Column(name = "start_time", nullable = false)
    private OffsetDateTime startTime = OffsetDateTime.now();

    @Column(name = "sport", nullable = false)
    private String sport = Sport.WALKING.name();

    @JdbcTypeCode(SqlTypes.INTERVAL_SECOND)
    @Column(name = "total_elapsed_time")
    private Duration totalElapsedTime;

    @JdbcTypeCode(SqlTypes.INTERVAL_SECOND)
    @Column(name = "total_timer_time")
    private Duration totalTimerTime;

    @Column(name = "total_distance")
    private Float totalDistance;

    @Column(name = "total_calories")
    private Integer totalCalories;

    @Column(name = "total_ascent")
    private Integer totalAscent;

    @Column(name = "enhanced_avg_speed")
    private Float enhancedAvgSpeed;

    @Column(name = "enhanced_max_speed")
    private Float enhancedMaxSpeed;

    @Column(name = "user_name", nullable = false)
    private String userName = "Misha";

    @Column(name = "device_name", nullable = false)
    private String deviceName = "App";

    @Column(name = "marked", nullable = false)
    private boolean marked;

    @OneToMany(mappedBy = "activity", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<Record> records = new ArrayList<>();

    public void addRecord(Record record) {
        records.add(record);
        record.setActivity(this);
    }
}
