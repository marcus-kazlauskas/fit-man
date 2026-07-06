package fit.man.app.repository.entity;

import fit.man.app.util.ActivityUtils;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Duration;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
@Entity
@Table(name = "analysis")
public class Analysis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private long id;

    @Column(name = "total_distance")
    private Float totalDistance;

    @JdbcTypeCode(SqlTypes.INTERVAL_SECOND)
    @Column(name = "moving_time")
    private Duration movingTime;

    @Column(name = "average_speed")
    private Float averageSpeed;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = ActivityUtils.ACTIVITY_TABLE_ID)
    @ToString.Exclude
    private Activity activity;
}
