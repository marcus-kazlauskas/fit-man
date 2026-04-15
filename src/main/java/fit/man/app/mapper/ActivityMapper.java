package fit.man.app.mapper;

import fit.man.app.api.model.ActivityResponse;
import fit.man.app.api.model.TrackResponse;
import fit.man.app.repository.entity.Activity;
import org.mapstruct.Mapper;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;

@FunctionalInterface
@Mapper(componentModel = "spring")
public interface ActivityMapper {
    ActivityResponse toResponse(Activity activity);

    default OffsetDateTime toOffsetDateTime(String dateTime) {
        return LocalDateTime.parse(dateTime)
                .atZone(ZoneId.systemDefault()).toOffsetDateTime();
    }

    default String toLocalDateTimeString(OffsetDateTime odt) {
        var adjustedOdt = odt.withOffsetSameInstant(
                ZoneId.systemDefault().getRules().getOffset(odt.toInstant())
        );
        return adjustedOdt.toLocalDateTime().toString();
    }

    default TrackResponse toTrackResponse(Activity activity) {
        var trackResponse = new TrackResponse();
        if (activity == null || activity.getRecords() == null) {
            return trackResponse;
        }
        trackResponse.setStartTime(
                toLocalDateTimeString(activity.getStartTime())
        );
        var points = activity.getRecords().stream()
                .filter(r -> r.getPositionLat() != null && r.getPositionLong() != null)
                .map(r -> List.of(r.getPositionLat(), r.getPositionLong()))
                .toList();
        trackResponse.setPoints(points);
        return trackResponse;
    }
}
