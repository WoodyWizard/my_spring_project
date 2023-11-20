package com.woody.shop.controller;


import com.woody.mydata.Order;
import com.woody.mydata.OrderValidException;
import com.woody.shop.service.OrderService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@AllArgsConstructor
public class OrderController {

    private OrderService orderService;

    @PostMapping("/order")
    public Order saveOrder(@RequestBody Order order) {
        return orderService.saveOrder(order);
    }


    @PostMapping("/request/order")
    public ResponseEntity<Order> createDeliveryRequest(@RequestBody Order order) {
        try {
            Order newOrder = orderService.createDeliveryRequest(order);
            return ResponseEntity.ok(orderService.updateStatus(order.getId(), newOrder.getStatus()));
        } catch (OrderValidException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/order/{id}")
    public ResponseEntity<Order> getOrder(Long id) {
        try {
            return ResponseEntity.ok(orderService.getOrder(id));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/order/{id}")
    public ResponseEntity<Order> updateOrder(@PathVariable("id") Long id, @RequestBody Order order) {
        try {
            return ResponseEntity.ok(orderService.updateOrder(id, order));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
