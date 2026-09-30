package fit.man.app.repository.entity

import com.garmin.fit.Sport
import fit.man.app.util.ActivityUtils
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne
import jakarta.persistence.OrderBy
import jakarta.persistence.PrePersist
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.Duration
import java.time.OffsetDateTime

@Entity
@Table(name = "activity")
class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    var id: Long = 0

    @Column(name = "end_time", nullable = false)
    var endTime: OffsetDateTime? = null

    @Column(name = "start_time", nullable = false)
    var startTime: OffsetDateTime? = null

    @Column(name = "sport", nullable = false)
    var sport: String? = null

    @JdbcTypeCode(SqlTypes.INTERVAL_SECOND)
    @Column(name = "total_elapsed_time")
    var totalElapsedTime: Duration? = null

    @JdbcTypeCode(SqlTypes.INTERVAL_SECOND)
    @Column(name = "total_timer_time")
    var totalTimerTime: Duration? = null

    @Column(name = "total_distance")
    var totalDistance: Float? = null

    @Column(name = "total_calories")
    var totalCalories: Int? = null

    @Column(name = "total_ascent")
    var totalAscent: Int? = null

    @Column(name = "enhanced_avg_speed")
    var enhancedAvgSpeed: Float? = null

    @Column(name = "enhanced_max_speed")
    var enhancedMaxSpeed: Float? = null

    @Column(name = "user_name", nullable = false)
    var userName: String? = null

    @Column(name = "device_name", nullable = false)
    var deviceName: String? = null

    @Column(name = "marked", nullable = false)
    var marked: Boolean = false

    @OneToMany(mappedBy = ActivityUtils.ACTIVITY_TABLE, cascade = [CascadeType.ALL], orphanRemoval = true)
    @OrderBy("positionTime")
    var records: MutableList<Record> = mutableListOf()

    fun addRecord(record: Record) {
        records.add(record)
        record.activity = this
    }

    @OneToMany(mappedBy = ActivityUtils.ACTIVITY_TABLE, cascade = [CascadeType.ALL], orphanRemoval = true)
    @OrderBy("eventTime")
    var events: MutableList<Event> = mutableListOf()

    fun addEvent(event: Event) {
        events.add(event)
        event.activity = this
    }

    @OneToOne(mappedBy = ActivityUtils.ACTIVITY_TABLE, cascade = [CascadeType.ALL], orphanRemoval = true)
    var analysis: Analysis? = null
        set(value) {
            field = value
            value?.activity = this
        }

    @PrePersist
    fun prePersist() {
        if (endTime == null) endTime = OffsetDateTime.now()
        if (startTime == null) startTime = OffsetDateTime.now()
        if (sport == null) sport = Sport.WALKING.name
        if (userName == null) userName = "Misha"
        if (deviceName == null) deviceName = "App"
    }

    override fun toString(): String {
        return "Activity(id=$id, endTime=$endTime, startTime=$startTime, sport=$sport, " +
                "totalElapsedTime=$totalElapsedTime, totalTimerTime=$totalTimerTime, totalDistance=$totalDistance, " +
                "totalCalories=$totalCalories, totalAscent=$totalAscent, enhancedAvgSpeed=$enhancedAvgSpeed, " +
                "enhancedMaxSpeed=$enhancedMaxSpeed, userName=$userName, deviceName=$deviceName, marked=$marked)"
    }
}
