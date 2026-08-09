package com.hcmut.divvy.service;

import com.hcmut.divvy.dto.response.CategoryResponse;
import com.hcmut.divvy.service.model.*;

import java.util.List;

public interface CategoryService {

    /**
     * Returns all expense categories available in the system.
     *
     * @return list of all categories
     */
    List<CategoryResponse> findAll();

    /**
     * Returns a single category by its ID.
     *
     * @param model contains the category ID
     * @return the matching category; throws 404 if not found
     */
    CategoryResponse findById(GetCategoryByIdModel model);

    /**
     * Creates a new expense category.
     *
     * @param model category name and optional icon
     * @return the newly created category
     */
    CategoryResponse create(CreateCategoryModel model);

    /**
     * Updates an existing category's name or icon.
     *
     * @param model category ID and updated fields
     * @return the updated category
     */
    CategoryResponse update(UpdateCategoryModel model);

    /**
     * Permanently deletes a category by its ID.
     *
     * @param model contains the category ID to delete
     */
    void delete(DeleteCategoryModel model);
}
