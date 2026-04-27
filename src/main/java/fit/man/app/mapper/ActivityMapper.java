package fit.man.app.mapper;

import fit.man.app.api.model.ActivityResponse;
import fit.man.app.api.model.TrackResponse;
import fit.man.app.repository.entity.Activity;
import fit.man.app.util.ActivityUtils;
import org.mapstruct.Mapper;

import java.util.List;

@FunctionalInterface
@Mapper(componentModel = "spring")
public interface ActivityMapper {
    ActivityResponse toResponse(Activity activity);

    default TrackResponse toTrackResponse(Activity activity) {
        var trackResponse = new TrackResponse();
        if (activity == null || activity.getRecords() == null) {
            return trackResponse;
        }
        trackResponse.setStartTime(
                ActivityUtils.toLocalDateTimeString(activity.getStartTime())
        );
        var points = activity.getRecords().stream()
                .filter(r -> r.getPositionLat() != null &&
                        r.getPositionLong() != null &&
                        r.getMark().equals(ActivityUtils.MARK_DEFAULT)
                )
                .map(r -> List.of(r.getPositionLat(), r.getPositionLong()))
                .toList();
        trackResponse.setPoints(points);
        return trackResponse;
    }
}
