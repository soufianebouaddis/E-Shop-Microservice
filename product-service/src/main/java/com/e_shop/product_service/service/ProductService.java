package com.e_shop.product_service.service;

import com.e_shop.product_service.dto.ProductDTO;
import com.e_shop.product_service.model.Product;

import java.util.List;

public interface ProductService {
    Product save(ProductDTO product);
    Product update(int id,ProductDTO product);
    Product delete (int id);
    List<Product> getProducts();
    Product getProduct(int id);
}
