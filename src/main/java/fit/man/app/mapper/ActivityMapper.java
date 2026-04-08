package fit.man.app.mapper;

import fit.man.app.api.model.ActivityResponse;
import fit.man.app.repository.entity.Activity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ActivityMapper {
    ActivityResponse toResponse(Activity activity);
}
