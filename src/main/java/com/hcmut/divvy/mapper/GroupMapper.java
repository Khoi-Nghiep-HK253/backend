package com.hcmut.divvy.mapper;

import com.hcmut.divvy.dto.response.GroupResponse;
import com.hcmut.divvy.entity.Category;
import com.hcmut.divvy.entity.Currency;
import com.hcmut.divvy.entity.Group;
import com.hcmut.divvy.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface GroupMapper {

    @Mapping(target = "category", source = "category")
    @Mapping(target = "defaultCurrency", source = "defaultCurrency")
    @Mapping(target = "createdBy", source = "createdBy")
    GroupResponse toResponse(Group group);

    default GroupResponse.CategoryInfo toCategoryInfo(Category category) {
        if (category == null) return null;
        return GroupResponse.CategoryInfo.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    default GroupResponse.CurrencyInfo toCurrencyInfo(Currency currency) {
        if (currency == null) return null;
        return GroupResponse.CurrencyInfo.builder()
                .id(currency.getId())
                .code(currency.getAcronym())
                .build();
    }

    default GroupResponse.UserInfo toUserInfo(User user) {
        if (user == null) return null;
        return GroupResponse.UserInfo.builder()
                .id(user.getId())
                .username(user.getUsername())
                .build();
    }
}
