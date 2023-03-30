package com.keremcengiz0.orderservice.repository;

import com.keremcengiz0.orderservice.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
