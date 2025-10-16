package com.fastcampus.ecommerce.controllers;

import com.fastcampus.ecommerce.models.ProductRequest;
import com.fastcampus.ecommerce.models.ProductResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> findProductById(@PathVariable(name = "id") Long productId){
        return ResponseEntity.ok(
                ProductResponse.builder()
                        .name("product" + productId)
                        .price(BigDecimal.ONE)
                        .description("deskripsi product")
                        .build()
        );
    }

    @GetMapping("")
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        return ResponseEntity.ok(
                List.of(
                        ProductResponse.builder()
                                .name("product")
                                .price(BigDecimal.ONE)
                                .description("deskripsi product")
                                .build(),
                        ProductResponse.builder()
                                .name("product")
                                .price(BigDecimal.ONE)
                                .description("deskripsi product")
                                .build()
                )
        );
    }

    @PostMapping("/")
    public ResponseEntity<ProductResponse> createProduct(@RequestBody @Valid ProductRequest request){
        return ResponseEntity.ok(
                ProductResponse.builder()
                        .name("product ")
                        .price(BigDecimal.ONE)
                        .description("deskripsi product")
                        .build()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@RequestBody @Valid ProductRequest request,
                                                         @PathVariable(name = "id") Long productId){
        return ResponseEntity.ok(
                ProductResponse.builder()
                        .name("product" + productId)
                        .price(BigDecimal.ONE)
                        .description("deskripsi product")
                        .build()
        );
    }
}
