package com.keith.orderservice.service;

import com.keith.orderservice.dto.OrderLineItemsDto;
import com.keith.orderservice.dto.OrderRequest;
import com.keith.orderservice.model.Order;
import com.keith.orderservice.model.OrderLineItems;
import com.keith.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient webClient;

    public void placeOrder(OrderRequest orderRequest){
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        List<OrderLineItems> orderLineItems = orderRequest
                .getOrderLineItemsDtoList()
                .stream()
                .map(this::mapToDto)
                .toList();
        order.setOrderLineItemsList(orderLineItems);
//        inventory service check if in stock
        Boolean result = webClient.get().uri("http://localhost:3003/api/v1/inventory")
                        .retrieve()
                        .bodyToMono(Boolean.class)
                        .block();
        if(Boolean.TRUE.equals(result)){
            orderRepository.save(order);
        }else{
            throw new IllegalArgumentException("Product is not in stock");
        }

        log.info("Order with order number {} is saved", order.getOrderNumber());
    }

    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
        return orderLineItems;
    }
}
