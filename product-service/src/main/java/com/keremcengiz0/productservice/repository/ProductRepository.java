package com.keremcengiz0.productservice.repository;

import com.keremcengiz0.productservice.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepository extends MongoRepository<Product, String> {
}
