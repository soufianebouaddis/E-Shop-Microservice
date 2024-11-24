package com.e_shop.product_service.service;

import com.e_shop.product_service.dto.CategoryDTO;
import com.e_shop.product_service.model.Category;

import java.util.List;

public interface CategoryService {
    Category save(CategoryDTO category);
    Category update(int id,CategoryDTO category);
    Category delete (int id);
    List<Category> getCategories();
    Category getCategory(int id);
}
