package com.e_shop.product_service.controller;

import com.e_shop.product_service.dto.CategoryDTO;
import com.e_shop.product_service.model.Category;
import com.e_shop.product_service.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/category")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/create-category")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> creatcategory(@RequestBody CategoryDTO categoryDTO) {
        try{
            return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.save(categoryDTO));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    @GetMapping("/get-categories")
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    public ResponseEntity<List<Category>> getcategorys() {
        return ResponseEntity.status(HttpStatus.OK).body(categoryService.getCategories());
    }
    @GetMapping("/get-category/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    public ResponseEntity<Category> getcategory(@PathVariable int id){
        return ResponseEntity.status(HttpStatus.OK).body(categoryService.getCategory(id));
    }
    @GetMapping("/delete-category/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    public ResponseEntity<?> deletecategory(@PathVariable int id){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(categoryService.delete(id));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    @GetMapping("/update-category/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    public ResponseEntity<?> updatecategory(@PathVariable int id, @RequestBody CategoryDTO categoryDTO){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(categoryService.update(id,categoryDTO));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

}
