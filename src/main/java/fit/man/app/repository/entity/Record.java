package fit.man.app.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
@Entity
@Table(name = "record")
public class Record {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "record_gen")
    @SequenceGenerator(name = "record_gen", sequenceName = "record_seq", allocationSize = 50)
    @Column(name = "id", nullable = false)
    private long id;

    @Column(name = "position_lat")
    private Double positionLat;

    @Column(name = "position_long")
    private Double positionLong;

    @Column(name = "distance")
    private Float distance;

    @Column(name = "enhanced_speed")
    private Float enhancedSpeed;

    @Column(name = "enhanced_altitude")
    private Float enhancedAltitude;

    @Column(name = "mark")
    private Short mark;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_id")
    @ToString.Exclude
    private Activity activity;
}
