package com.e_shop.product_service.mapper.impl;

import com.e_shop.product_service.dto.ProductDTO;
import com.e_shop.product_service.mapper.service.Mapper;
import com.e_shop.product_service.model.Product;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper implements Mapper<Product, ProductDTO> {
    private final ModelMapper modelMapper;

    public ProductMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public ProductDTO fromEntity(Product product) {
        return this.modelMapper.map(product,ProductDTO.class);
    }

    @Override
    public Product toEntity(ProductDTO productDTO) {
        return this.modelMapper.map(productDTO,Product.class);
    }
}
