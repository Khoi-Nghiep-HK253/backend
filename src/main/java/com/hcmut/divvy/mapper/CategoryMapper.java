package com.hcmut.divvy.mapper;

import com.hcmut.divvy.dto.request.CreateCategoryRequest;
import com.hcmut.divvy.dto.request.UpdateCategoryRequest;
import com.hcmut.divvy.dto.response.CategoryResponse;
import com.hcmut.divvy.entity.Category;
import com.hcmut.divvy.service.model.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    default GetCategoryByIdModel toGetCategoryByIdModel(Integer id) {
        return GetCategoryByIdModel.builder().id(id).build();
    }

    default DeleteCategoryModel toDeleteCategoryModel(Integer id) {
        return DeleteCategoryModel.builder().id(id).build();
    }

    CreateCategoryModel toModel(CreateCategoryRequest request);

    @Mapping(target = "id", source = "id")
    UpdateCategoryModel toModel(UpdateCategoryRequest request, Integer id);

    CategoryResponse toResponse(Category category);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Category toEntity(CreateCategoryModel model);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updatePartial(UpdateCategoryModel model, @MappingTarget Category category);
}
