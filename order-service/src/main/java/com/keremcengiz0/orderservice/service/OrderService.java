package com.keremcengiz0.orderservice.service;

import com.keremcengiz0.orderservice.dto.OrderRequest;
import com.keremcengiz0.orderservice.model.Order;
import com.keremcengiz0.orderservice.model.OrderLineItems;
import com.keremcengiz0.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final ModelMapper modelMapper;
    private final OrderRepository orderRepository;

    public void placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(orderLineItemsDto -> modelMapper.map(orderLineItemsDto, OrderLineItems.class)).collect(Collectors.toList());

        order.setOrderLineItemsList(orderLineItems);

        this.orderRepository.save(order);
    }
}
