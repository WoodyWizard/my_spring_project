package com.woody.shop.service;


import com.woody.mydata.Order;
import com.woody.mydata.OrderValidException;
import com.woody.shop.repository.OrderRepository;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

@Service
public class OrderService {

    private OrderRepository orderRepository;

    private RestTemplate restTemplate;

    @Autowired
    public OrderService(OrderRepository orderRepository, RestTemplateBuilder restTemplateBuilder) {
        this.orderRepository = orderRepository;
        this.restTemplate = restTemplateBuilder
                .rootUri("http://localhost:8083")
                .build();
    }


    public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }

    public Order createDeliveryRequest(Order order) {
        ResponseEntity<Order> responseEntity = restTemplate.postForEntity("/request/delivery", order, Order.class);


        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            return responseEntity.getBody();
        } else {
            throw new OrderValidException();
        }
    }

    public Order getOrder(Long id) {
        Optional<Order> order = orderRepository.findById(id);
        return order.orElseThrow(() -> new NoSuchElementException());
    }

    public Order updateStatus(Long id, String status) {
        Order order = getOrder(id);
        order.setStatus(status);
        return orderRepository.save(order);
    }

    public Order updateOrder(Long id, Order order) {
        Order updatedOrder = getOrder(id);
        if (Objects.nonNull(order.getAddress()) && !"".equalsIgnoreCase(updatedOrder.getAddress()) ) {
            updatedOrder.setAddress(order.getAddress());
        }
        if (Objects.nonNull(order.getStatus()) && !"".equalsIgnoreCase(updatedOrder.getStatus()) ) {
            updatedOrder.setStatus(order.getStatus());
        }
        if (Objects.nonNull(order.getCustomer())) {
            updatedOrder.setCustomer(order.getCustomer());
        }
        if (Objects.nonNull(order.getDeliverChecker())) {
            updatedOrder.setDeliverChecker(order.getDeliverChecker());
        }
        if (Objects.nonNull(order.getClientChecker())) {
            updatedOrder.setClientChecker(order.getClientChecker());
        }
        if (Objects.nonNull(order.getDiscount())) {
            updatedOrder.setDiscount(order.getDiscount());
        }
        if (Objects.nonNull(order.getTotal())) {
            updatedOrder.setTotal(order.getTotal());
        }
        if (Objects.nonNull(order.getPaymentMethod()) && !"".equalsIgnoreCase(updatedOrder.getPaymentMethod())) {
            updatedOrder.setPaymentMethod(order.getPaymentMethod());
        }
        if (Objects.nonNull(order.getPaymentStatus()) && !"".equalsIgnoreCase(updatedOrder.getPaymentStatus())) {
            updatedOrder.setPaymentStatus(order.getPaymentStatus());
        }
        if (Objects.nonNull(order.getItems())) {
            updatedOrder.setItems(order.getItems());
        }
        if (Objects.nonNull(order.getShipping())) {
            updatedOrder.setShipping(order.getShipping());
        }
        return orderRepository.save(updatedOrder);
    }
}
