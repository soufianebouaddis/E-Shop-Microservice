package com.e_shop.product_service.controller;

import com.e_shop.product_service.dto.ProductDTO;
import com.e_shop.product_service.model.Product;
import com.e_shop.product_service.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api/v1/product")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/create-product")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> creatProduct(ProductDTO productDTO) {
        try{
            return ResponseEntity.status(HttpStatus.CREATED).body(productService.save(productDTO));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    @GetMapping("/get-product")
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    public ResponseEntity<List<Product>> getProducts() {
        return ResponseEntity.status(HttpStatus.OK).body(productService.getProducts());
    }
    @GetMapping("/get-product/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    public ResponseEntity<Product> getProduct(@PathVariable int id){
        return ResponseEntity.status(HttpStatus.OK).body(productService.getProduct(id));
    }
    @GetMapping("/delete-product/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    public ResponseEntity<?> deleteProduct(@PathVariable int id){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(productService.delete(id));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

}
