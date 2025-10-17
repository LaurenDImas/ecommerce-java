package com.fastcampus.ecommerce.controller;

import com.fastcampus.ecommerce.model.PaginatedProductResponse;
import com.fastcampus.ecommerce.model.ProductRequest;
import com.fastcampus.ecommerce.model.ProductResponse;
import com.fastcampus.ecommerce.service.ProductService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/products")
@SecurityRequirement(name = "bearer")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/")
    public ResponseEntity<PaginatedProductResponse> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "product_id,asc") String[] sort,
            @RequestParam(required = false) String name) {
        List<Sort.Order> orders = new ArrayList<>();
        if (sort[0].contains(",")){
            for (String sortOrder : sort) {
                String[] _sort =  sortOrder.split(",");
                orders.add(new Sort.Order(getSortDirection(_sort[1]), _sort[0]));
            }
        }else{
            orders.add(new Sort.Order(getSortDirection(sort[1]), sort[0]));
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by(orders));
        Page<ProductResponse> productResponse;

        if (name != null && !name.isEmpty()) {
            productResponse = productService.findByNameAndPagable(name, pageable);
        } else {
            productResponse = productService.findByPage(pageable);
        }

        return ResponseEntity.ok(productService.convertProductPage(productResponse));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> findProductById(@PathVariable(name = "id") Long productId){
        ProductResponse productResponse = productService.findById(productId);
        return ResponseEntity.ok(productResponse);
    }

    @PostMapping("/")
    public ResponseEntity<ProductResponse> createProduct(@RequestBody @Valid ProductRequest request){
        ProductResponse productResponse = productService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(productResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@RequestBody @Valid ProductRequest request,
                                                         @PathVariable(name = "id") Long productId){
        ProductResponse productResponse = productService.update(productId, request);
        return ResponseEntity.ok(productResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable(name = "id") Long productId){
        productService.delete(productId);
        return ResponseEntity.noContent().build();
    }

    private Sort.Direction getSortDirection(String direction) {
        if (direction.equals("desc")){
            return Sort.Direction.DESC;
        }

        return Sort.Direction.ASC;
    }
}
