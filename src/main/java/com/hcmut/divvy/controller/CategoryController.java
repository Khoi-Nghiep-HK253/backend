package com.hcmut.divvy.controller;

import com.hcmut.divvy.common.dto.ApiResponse;
import com.hcmut.divvy.dto.request.CreateCategoryRequest;
import com.hcmut.divvy.dto.request.UpdateCategoryRequest;
import com.hcmut.divvy.dto.response.CategoryResponse;
import com.hcmut.divvy.mapper.CategoryMapper;
import com.hcmut.divvy.service.CategoryService;
import com.hcmut.divvy.service.model.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "Category Management", description = "APIs for listing, viewing, creating, updating, and deleting categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;

    /**
     * Retrieve a list of all categories.
     *
     * @return {@code 200 OK} with a list of CategoryResponse
     */
    @GetMapping
    @Operation(summary = "Get list of all categories", description = "Retrieves all categories available in the system")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories() {
        List<CategoryResponse> categories = categoryService.findAll();
        return ResponseEntity.ok(ApiResponse.ok(categories, "Categories retrieved successfully"));
    }

    /**
     * Retrieve details of a specific category by its ID.
     *
     * @param id the category ID
     * @return {@code 200 OK} with CategoryResponse; {@code 404} if not found
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get category details", description = "Retrieves detailed information for a single category record by ID")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryById(@PathVariable Integer id) {
        GetCategoryByIdModel model = categoryMapper.toGetCategoryByIdModel(id);
        CategoryResponse category = categoryService.findById(model);
        return ResponseEntity.ok(ApiResponse.ok(category, "Category retrieved successfully"));
    }

    /**
     * Create a new category.
     *
     * @param request the category creation request payload (name, icon)
     * @return {@code 201 Created} with CategoryResponse; {@code 409} if category
     *         name already exists
     */
    @PostMapping
    @Operation(summary = "Create a new category", description = "Creates a new category with a unique name and optional icon")
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(
            @Valid @RequestBody CreateCategoryRequest request) {
        CreateCategoryModel model = categoryMapper.toModel(request);
        CategoryResponse created = categoryService.create(model);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(created, "Category created successfully"));
    }

    /**
     * Update an existing category.
     *
     * @param id      the ID of the category to update
     * @param request the fields to update (name, icon)
     * @return {@code 200 OK} with the updated CategoryResponse;
     *         {@code 404} if category not found; {@code 409} if the new name
     *         already exists
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update a category", description = "Updates an existing category's name or icon")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(
            @PathVariable Integer id,
            @RequestBody UpdateCategoryRequest request) {
        UpdateCategoryModel model = categoryMapper.toModel(request, id);
        CategoryResponse updated = categoryService.update(model);
        return ResponseEntity.ok(ApiResponse.ok(updated, "Category updated successfully"));
    }

    /**
     * Delete a category by ID.
     *
     * @param id the category ID to delete
     * @return {@code 200 OK}; {@code 404} if not found
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a category", description = "Deletes a category record by ID")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Integer id) {
        DeleteCategoryModel model = categoryMapper.toDeleteCategoryModel(id);
        categoryService.delete(model);
        return ResponseEntity.ok(ApiResponse.ok(null, "Category deleted successfully"));
    }
}
