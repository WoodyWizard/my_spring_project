package com.woody.delivery.controller;

import com.woody.delivery.service.DeliveryService;
import com.woody.mydata.Order;
import com.woody.mydata.OrderValidException;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.NoSuchElementException;

@RestController
@AllArgsConstructor
public class DeliveryController {

    private DeliveryService deliveryService;

    @GetMapping("/delivery/{id}")
    public ResponseEntity<Order> delivery(@PathVariable("id") Long id) {
        try {
            Order order = deliveryService.foundOrder(id);
            return ResponseEntity.ok(order);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Return 500 Internal Server Error
        }
    }

    @PostMapping("/request/delivery")
    public ResponseEntity<Order> createDeliveryRequest(@RequestBody Order order) {
        try {
            Order processedOrder = deliveryService.acceptOrder(order);
            deliveryService.syncWithShop(processedOrder);
            return ResponseEntity.ok(processedOrder);
        } catch (OrderValidException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Return 500 Internal Server Error
        }
    }


    @GetMapping("/request/finish/{id}")
    public ResponseEntity<Order> finishDelivery(@PathVariable("id") Long id) {
        try {
            Order order = deliveryService.finishDelivery(id);
            Order finishedOrder = deliveryService.saveToGDatabase(order);
            deliveryService.deleteOrder(id);
            return ResponseEntity.ok(finishedOrder);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Return 500 Internal Server Error
        }
    }
}
