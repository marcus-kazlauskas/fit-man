package fit.man.app.repository.entity

import fit.man.app.util.ActivityUtils
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "record")
class Record {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "record_gen")
    @SequenceGenerator(name = "record_gen", sequenceName = "record_seq", allocationSize = 50)
    @Column(name = "id", nullable = false)
    var id: Long = 0

    @Column(name = "position_time")
    var positionTime: LocalDateTime? = null

    @Column(name = "position_lat")
    var positionLat: Double? = null

    @Column(name = "position_long")
    var positionLong: Double? = null

    @Column(name = "distance")
    var distance: Float? = null

    @Column(name = "enhanced_speed")
    var enhancedSpeed: Float? = null

    @Column(name = "enhanced_altitude")
    var enhancedAltitude: Float? = null

    @Column(name = "mark")
    var mark: Short? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = ActivityUtils.ACTIVITY_TABLE_ID)
    var activity: Activity? = null

    override fun toString(): String {
        return "Record(id=$id, positionTime=$positionTime, positionLat=$positionLat, " +
                "positionLong=$positionLong, distance=$distance, enhancedSpeed=$enhancedSpeed, " +
                "enhancedAltitude=$enhancedAltitude, mark=$mark)"
    }
}
