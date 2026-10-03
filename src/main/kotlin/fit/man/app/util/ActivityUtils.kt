package fit.man.app.util

import fit.man.app.repository.entity.Record
import net.sf.geographiclib.Geodesic
import java.time.Duration
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId

object ActivityUtils {
    const val ACTIVITY_TABLE = "activity"
    const val ACTIVITY_TABLE_ID = "activity_id"

    const val MILLIS = 1000
    const val KM_PER_HOUR = 3.6
    const val MARK_DISABLED: Short = 0
    const val MARK_DEFAULT: Short = 1

    @JvmField
    val DECIMAL_DEGREES = 180.0 / Math.pow(2.0, 31.0)

    @JvmStatic
    fun toOffsetDateTime(dateTime: String): OffsetDateTime =
        LocalDateTime
            .parse(dateTime)
            .atZone(ZoneId.systemDefault())
            .toOffsetDateTime()

    @JvmStatic
    fun toLocalDateTimeString(odt: OffsetDateTime): String {
        val adjustedOdt =
            odt.withOffsetSameInstant(
                ZoneId.systemDefault().rules.getOffset(odt.toInstant()),
            )
        return adjustedOdt.toLocalDateTime().toString()
    }

    @JvmStatic
    fun calcDistance(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double,
    ): Double = Geodesic.WGS84.Inverse(lat1, lon1, lat2, lon2).s12

    @JvmStatic
    fun calcSpeed(
        rec1: Record,
        rec2: Record,
    ): Double {
        val dist = calcDistance(rec1.positionLat!!, rec1.positionLong!!, rec2.positionLat!!, rec2.positionLong!!)
        val time = Duration.between(rec1.positionTime, rec2.positionTime).toMillis() / MILLIS.toDouble()
        if (time == 0.0) {
            return 0.0
        }
        return dist / time * KM_PER_HOUR
    }

    @JvmStatic
    fun positionIsNull(rec: Record): Boolean = rec.positionLat == null || rec.positionLong == null || rec.positionTime == null
}
