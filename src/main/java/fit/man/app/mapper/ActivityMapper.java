package fit.man.app.mapper;

import fit.man.app.api.model.ActivityResponse;
import fit.man.app.repository.entity.Activity;
import org.mapstruct.Mapper;

@FunctionalInterface
@Mapper(componentModel = "spring")
public interface ActivityMapper {
    ActivityResponse toResponse(Activity activity);

    default double[][] toPointsArray(Activity activity) {
        if (activity == null || activity.getRecords() == null) {
            return new double[0][0];
        }
        return activity.getRecords().stream()
                .filter(r -> r.getPositionLat() != null && r.getPositionLong() != null)
                .map(r -> new double[]{r.getPositionLat(), r.getPositionLong()})
                .toArray(double[][]::new);
    }
}
