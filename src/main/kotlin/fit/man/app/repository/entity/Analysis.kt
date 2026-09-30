package fit.man.app.repository.entity

import fit.man.app.util.ActivityUtils
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table

@Entity
@Table(name = "analysis")
class Analysis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    var id: Long = 0

    @Column(name = "total_distance")
    var totalDistance: Float? = null

    @Column(name = "moving_time")
    var movingTime: Long? = null

    @Column(name = "average_speed")
    var averageSpeed: Float? = null

    @Column(name = "success")
    var success: Boolean = false

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = ActivityUtils.ACTIVITY_TABLE_ID)
    var activity: Activity? = null

    override fun toString(): String {
        return "Analysis(id=$id, totalDistance=$totalDistance, movingTime=$movingTime, " +
                "averageSpeed=$averageSpeed, success=$success)"
    }
}
