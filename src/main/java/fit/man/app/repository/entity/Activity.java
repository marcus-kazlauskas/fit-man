package fit.man.app.repository.entity;

import com.garmin.fit.Sport;
import fit.man.app.util.ActivityUtils;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrePersist;
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
    private OffsetDateTime endTime;

    @Column(name = "start_time", nullable = false)
    private OffsetDateTime startTime;

    @Column(name = "sport", nullable = false)
    private String sport;

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
    private String userName;

    @Column(name = "device_name", nullable = false)
    private String deviceName;

    @Column(name = "marked", nullable = false)
    private boolean marked;

    @OneToMany(mappedBy = ActivityUtils.ACTIVITY_TABLE, cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @OrderBy("positionTime")
    private List<Record> records = new ArrayList<>();

    public void addRecord(Record record) {
        records.add(record);
        record.setActivity(this);
    }

    @OneToMany(mappedBy = ActivityUtils.ACTIVITY_TABLE, cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @OrderBy("eventTime")
    private List<Event> events = new ArrayList<>();

    public void addEvent(Event event) {
        events.add(event);
        event.setActivity(this);
    }

    @OneToOne(mappedBy = ActivityUtils.ACTIVITY_TABLE, cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private Analysis analysis;

    public void setAnalysis(Analysis analysis) {
        this.analysis = analysis;
        analysis.setActivity(this);
    }

    @PrePersist
    public void prePersist() {
        if (endTime == null) endTime = OffsetDateTime.now();
        if (startTime == null) startTime = OffsetDateTime.now();
        if (sport == null) sport = Sport.WALKING.name();
        if (userName == null) userName = "Misha";
        if (deviceName == null) deviceName = "App";
    }
}
