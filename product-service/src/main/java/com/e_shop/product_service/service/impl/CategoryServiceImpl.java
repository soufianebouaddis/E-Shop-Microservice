package com.e_shop.product_service.service.impl;

import com.e_shop.product_service.dto.CategoryDTO;
import com.e_shop.product_service.exception.CategoryNotFound;
import com.e_shop.product_service.mapper.impl.CategoryMapper;
import com.e_shop.product_service.model.Category;
import com.e_shop.product_service.repository.CategoryRepository;
import com.e_shop.product_service.service.CategoryService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryMapper categoryMapper;
    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryMapper categoryMapper, CategoryRepository categoryRepository) {
        this.categoryMapper = categoryMapper;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Category save(CategoryDTO category) {
        Category newCategory = this.categoryMapper.toEntity(category);
        newCategory.setCreatedAt(LocalDate.now());
        return this.categoryRepository.save(newCategory);
    }

    @Override
    public Category update(int id, CategoryDTO category) {
        return this.categoryRepository.findById(id).map(category_iter->{
            category_iter.setUpdatedAt(LocalDate.now());
            category_iter.setName(category.getName());
            return this.categoryRepository.save(category_iter);
        }).orElseThrow(()-> new CategoryNotFound("Category not found with id => "+id));
    }

    @Override
    public Category delete(int id) {
        Category deletedCategory = this.categoryRepository
                .findById(id)
                .orElseThrow(()-> new CategoryNotFound("Category not found with id => "+id));
        this.categoryRepository.deleteById(id);
        return deletedCategory;
    }

    @Override
    public List<Category> getCategories() {
        List<Category> categories = this.categoryRepository.findAll();
        return categories.isEmpty() ? Collections.emptyList():categories;
    }

    @Override
    public Category getCategory(int id) {
        return null;
    }
}
