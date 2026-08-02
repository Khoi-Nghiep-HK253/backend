package com.hcmut.divvy.service;

import com.hcmut.divvy.dto.response.CategoryResponse;
import com.hcmut.divvy.service.model.*;

import java.util.List;

public interface CategoryService {
    List<CategoryResponse> findAll();
    CategoryResponse findById(GetCategoryByIdModel model);
    CategoryResponse create(CreateCategoryModel model);
    CategoryResponse update(UpdateCategoryModel model);
    void delete(DeleteCategoryModel model);
}
