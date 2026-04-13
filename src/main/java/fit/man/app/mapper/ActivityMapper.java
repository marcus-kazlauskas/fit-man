package fit.man.app.mapper;

import fit.man.app.api.model.ActivityResponse;
import fit.man.app.repository.entity.Activity;
import org.mapstruct.Mapper;

import java.util.List;

@FunctionalInterface
@Mapper(componentModel = "spring")
public interface ActivityMapper {
    ActivityResponse toResponse(Activity activity);

    default List<List<Double>> toPointsList(Activity activity) {
        if (activity == null || activity.getRecords() == null) {
            return List.of(List.of());
        }
        return activity.getRecords().stream()
                .filter(r -> r.getPositionLat() != null && r.getPositionLong() != null)
                .map(r -> List.of(r.getPositionLat(), r.getPositionLong()))
                .toList();
    }
}
