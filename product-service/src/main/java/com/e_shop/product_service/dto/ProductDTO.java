package com.e_shop.product_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductDTO {
    @NotNull(message = "Name is mandatory")
    private String name;
    @NotNull(message = "Image is mandatory")
    private String image;
    @NotNull(message = "Reference is mandatory")
    private String reference;
    @NotNull(message = "Price is mandatory")
    private BigDecimal price;
}
