package com.hcmut.divvy.mapper;

import com.hcmut.divvy.dto.request.SurveyRequest;
import com.hcmut.divvy.dto.response.SurveyResponse;
import com.hcmut.divvy.entity.Survey;
import com.hcmut.divvy.service.model.CreateSurveyModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SurveyMapper {

    CreateSurveyModel toModel(SurveyRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Survey toEntity(CreateSurveyModel model);

    SurveyResponse toResponse(Survey entity);
}