package com.hcmut.divvy.mapper;

import com.hcmut.divvy.dto.request.CreateCurrencyRequest;
import com.hcmut.divvy.dto.request.UpdateCurrencyRequest;
import com.hcmut.divvy.dto.response.CurrencyResponse;
import com.hcmut.divvy.entity.Currency;
import com.hcmut.divvy.service.model.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CurrencyMapper {

    default GetCurrencyByIdModel toGetCurrencyByIdModel(Integer id) {
        return GetCurrencyByIdModel.builder().id(id).build();
    }

    default DeleteCurrencyModel toDeleteCurrencyModel(Integer id) {
        return DeleteCurrencyModel.builder().id(id).build();
    }

    CreateCurrencyModel toModel(CreateCurrencyRequest request);

    @Mapping(target = "id", source = "id")
    UpdateCurrencyModel toModel(UpdateCurrencyRequest request, Integer id);

    @Mapping(target = "code", source = "acronym")
    CurrencyResponse toResponse(Currency currency);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Currency toEntity(CreateCurrencyModel model);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updatePartial(UpdateCurrencyModel model, @MappingTarget Currency currency);
}
