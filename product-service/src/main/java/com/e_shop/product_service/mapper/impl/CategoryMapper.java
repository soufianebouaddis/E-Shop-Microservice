package com.e_shop.product_service.mapper.impl;

import com.e_shop.product_service.dto.CategoryDTO;
import com.e_shop.product_service.mapper.service.Mapper;
import com.e_shop.product_service.model.Category;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper implements Mapper<Category, CategoryDTO> {
    private final ModelMapper modelMapper;

    public CategoryMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public CategoryDTO fromEntity(Category category) {
        return modelMapper.map(category,CategoryDTO.class);
    }

    @Override
    public Category toEntity(CategoryDTO categoryDTO) {
        return modelMapper.map(categoryDTO,Category.class);
    }
}
