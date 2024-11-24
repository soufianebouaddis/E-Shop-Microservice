package com.e_shop.product_service.service.impl;

import com.e_shop.product_service.dto.ProductDTO;
import com.e_shop.product_service.exception.ProductNotFound;
import com.e_shop.product_service.mapper.impl.ProductMapper;
import com.e_shop.product_service.model.Product;
import com.e_shop.product_service.repository.ProductRepository;
import com.e_shop.product_service.service.ProductService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    public ProductServiceImpl(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    @Override
    public Product save(ProductDTO product) {
        Product newProduct = this.productMapper.toEntity(product);
        newProduct.setCreatedAt(LocalDate.now());
        return this.productRepository.save(newProduct);
    }

    @Override
    public Product update(int id, ProductDTO product) {
        return this.productRepository.findById(id).map(product_iter->{
            product_iter.setPrice(product.getPrice());
            product_iter.setImage(product.getImage());
            product_iter.setReference(product.getReference());
            product_iter.setName(product.getName());
            product_iter.setUpdatedAt(LocalDate.now());
            return this.productRepository.save(product_iter);
        }).orElseThrow(()-> new ProductNotFound("Product not found with id => "+id));
    }

    @Override
    public Product delete(int id) {
        Product deletedProduct = this.productRepository
                .findById(id)
                .orElseThrow(()-> new ProductNotFound("Product not found with id => "+id));
        this.productRepository.deleteById(id);
        return deletedProduct;
    }

    @Override
    public List<Product> getProducts() {
        List<Product> products = this.productRepository.findAll();
        return products.isEmpty() ? Collections.emptyList():products;
    }

    @Override
    public Product getProduct(int id) {
        return this.productRepository
                .findById(id)
                .orElseThrow(()-> new ProductNotFound("Product not found with id => "+id));
    }
}
