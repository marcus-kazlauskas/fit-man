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
@Table(name = "event")
class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "event_gen")
    @SequenceGenerator(name = "event_gen", sequenceName = "event_seq", allocationSize = 50)
    @Column(name = "id", nullable = false)
    var id: Long = 0

    @Column(name = "event_time")
    var eventTime: LocalDateTime? = null

    @Column(name = "event_name")
    var eventName: String? = null

    @Column(name = "event_type")
    var eventType: String? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = ActivityUtils.ACTIVITY_TABLE_ID)
    var activity: Activity? = null

    override fun toString(): String {
        return "Event(id=$id, eventTime=$eventTime, eventName=$eventName, eventType=$eventType)"
    }
}
