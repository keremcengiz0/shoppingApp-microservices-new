package com.keremcengiz0.productservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.keremcengiz0.productservice.dto.ProductCreateRequest;
import com.keremcengiz0.productservice.dto.ProductResponse;
import com.keremcengiz0.productservice.model.Product;
import com.keremcengiz0.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    public void createProduct(ProductCreateRequest productCreateRequest) {
        Product product = Product.builder()
                .name(productCreateRequest.getName())
                .description(productCreateRequest.getDescription())
                .price(productCreateRequest.getPrice())
                .build();

        this.productRepository.save(product);
        //log.info("Product " + product.getId() + " is saved");
        log.info("Product {} is saved", product.getId());
    }

    public List<ProductResponse> getAllProducts() {
        List<Product> products = this.productRepository.findAll();

        return products.stream().map(product -> modelMapper.map(product, ProductResponse.class)).collect(Collectors.toList());
    }
}
