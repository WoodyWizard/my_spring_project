package com.woody.delivery.controller;

import com.woody.delivery.service.DeliveryService;
import com.woody.mydata.Deliverer;
import com.woody.mydata.Order;
import com.woody.mydata.OrderValidException;
import com.woody.mydata.User;
import jakarta.validation.Valid;
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

/*    @GetMapping("/hello")
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok(deliveryService.HelloOverRestTemplate());
    }*/

    @GetMapping("/test/user/{id}")
    public ResponseEntity<User> testUser(@PathVariable("id") Long id) {
        try {
            User user = deliveryService.findUserById(id);
            return ResponseEntity.ok(user);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Return 500 Internal Server Error
        }
    }

    @GetMapping("/delivery/{id}")
    public ResponseEntity<Order> delivery(@PathVariable("id") Long id) {
        try {
            Order order = deliveryService.findOrderById(id);
            return ResponseEntity.ok(order);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Return 500 Internal Server Error
        }
    }






    @PostMapping("/save/deliverer")
    public ResponseEntity<Deliverer> registerDeliverer(@Valid @RequestBody Deliverer deliverer) {
        try {
            Deliverer registeredDeliverer = deliveryService.saveDelivererToDB(deliverer);
            return ResponseEntity.ok(registeredDeliverer);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Return 500 Internal Server Error
        }
    }

    @GetMapping("/deliverer/{id}")
    public ResponseEntity<Deliverer> deliverer(@PathVariable("id") Long id) {
        try {
            Deliverer deliverer = deliveryService.getDelivererById(id);
            return ResponseEntity.ok(deliverer);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Return 500 Internal Server Error
        }
    }

    @DeleteMapping("/delete/deliverer/{id}")
    public ResponseEntity<Deliverer> deleteDeliverer(@PathVariable("id") Long id) {
        try {
            Deliverer deliverer = deliveryService.deleteDelivererById(id);
            return ResponseEntity.ok(deliverer);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Return 500 Internal Server Error
        }
    }











    @PostMapping("/request/delivery")
    public ResponseEntity<Order> createDeliveryRequest(@Valid @RequestBody Order order) {
        try {
            Order processedOrder = deliveryService.acceptOrder(order);
            deliveryService.saveOrderToDB(processedOrder);
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
            return ResponseEntity.ok(order);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Return 500 Internal Server Error
        }
    }
}
