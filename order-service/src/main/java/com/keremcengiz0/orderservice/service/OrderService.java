package com.keremcengiz0.orderservice.service;

import com.keremcengiz0.orderservice.dto.InventoryResponse;
import com.keremcengiz0.orderservice.dto.OrderRequest;
import com.keremcengiz0.orderservice.event.OrderPlacedEvent;
import com.keremcengiz0.orderservice.model.Order;
import com.keremcengiz0.orderservice.model.OrderLineItems;
import com.keremcengiz0.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final ModelMapper modelMapper;
    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;
    private final Tracer tracer;
    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    public String placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(orderLineItemsDto -> modelMapper.map(orderLineItemsDto, OrderLineItems.class)).collect(Collectors.toList());

        order.setOrderLineItemsList(orderLineItems);

        List<String> skuCodes = order.getOrderLineItemsList().stream().map(OrderLineItems::getSkuCode).collect(Collectors.toList());

        Span inventoryServiceLookup = tracer.nextSpan().name("InventoryServiceLookup");

        try (Tracer.SpanInScope spanInScope = tracer.withSpan(inventoryServiceLookup.start())) {
            InventoryResponse[] inventoryResponseArray = webClientBuilder.build().get()
                    .uri("http://inventory-service/api/inventory",
                            uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
                    .retrieve()
                    .bodyToMono(InventoryResponse[].class)
                    .block();

            boolean allProductsInStock = Arrays.stream(inventoryResponseArray).allMatch(InventoryResponse::isInStock);

            if (Boolean.FALSE.equals(allProductsInStock)) {
                throw new IllegalArgumentException("Product is not in stock, please try again later");
            }

            this.orderRepository.save(order);
            kafkaTemplate.send("notificationTopic", new OrderPlacedEvent(order.getOrderNumber()));

            return "Order Placed Successfully";

        } finally {
            inventoryServiceLookup.end();
        }


    }

}
