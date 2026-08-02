package com.hcmut.divvy.service.impl;

import com.hcmut.divvy.dto.response.CategoryResponse;
import com.hcmut.divvy.entity.Category;
import com.hcmut.divvy.mapper.CategoryMapper;
import com.hcmut.divvy.repository.CategoryRepository;
import com.hcmut.divvy.service.CategoryService;
import com.hcmut.divvy.service.model.*;
import com.hcmut.divvy.validator.CategoryValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryValidator categoryValidator;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> findAll() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse findById(GetCategoryByIdModel model) {
        Category category = categoryValidator.validateCategoryExists(
                categoryRepository.findById(model.getId()), model.getId());
        return categoryMapper.toResponse(category);
    }

    @Override
    @Transactional
    public CategoryResponse create(CreateCategoryModel model) {
        boolean nameExists = categoryRepository.findByName(model.getName()).isPresent();
        categoryValidator.validateCategoryNameUnique(nameExists);

        Category category = categoryMapper.toEntity(model);
        category = categoryRepository.save(category);
        return categoryMapper.toResponse(category);
    }

    @Override
    @Transactional
    public CategoryResponse update(UpdateCategoryModel model) {
        Category category = categoryValidator.validateCategoryExists(
                categoryRepository.findById(model.getId()), model.getId());

        if (model.getName() != null && !model.getName().equalsIgnoreCase(category.getName())) {
            boolean nameExists = categoryRepository.findByName(model.getName()).isPresent();
            categoryValidator.validateCategoryNameUnique(nameExists);
        }

        categoryMapper.updatePartial(model, category);
        category = categoryRepository.save(category);
        return categoryMapper.toResponse(category);
    }

    @Override
    @Transactional
    public void delete(DeleteCategoryModel model) {
        Category category = categoryValidator.validateCategoryExists(
                categoryRepository.findById(model.getId()), model.getId());
        categoryRepository.delete(category);
    }
}
