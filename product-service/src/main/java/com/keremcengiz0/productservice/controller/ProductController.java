package com.keremcengiz0.productservice.controller;

import com.keremcengiz0.productservice.dto.ProductCreateRequest;
import com.keremcengiz0.productservice.dto.ProductResponse;
import com.keremcengiz0.productservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createProduct(@RequestBody ProductCreateRequest productCreateRequest) {
        this.productService.createProduct(productCreateRequest);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ProductResponse> getAllProducts() {
        return this.productService.getAllProducts();
    }
}
