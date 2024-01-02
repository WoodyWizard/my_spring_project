package com.woody.shop.controller;


import com.woody.mydata.Order;
import com.woody.mydata.User;
import com.woody.shop.service.ShopService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@AllArgsConstructor
public class ShopController {

    private ShopService shopService;

    @GetMapping("/user/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") Long id) {
        try {
            return ResponseEntity.ok().body(shopService.findUserById(id));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Return 500 Internal Server Error
        }
    }

    @PostMapping("/save/user")
    public ResponseEntity<User> saveUser(@Valid @RequestBody User user) {
        try {
            return ResponseEntity.ok().body(shopService.saveUserToDB(user));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Return 500 Internal Server Error
        }
    }

    @DeleteMapping("/delete/user/{id}")
    public ResponseEntity<User> deleteUser(@PathVariable("id") Long id) {
        try {
            User user = shopService.deleteUserById(id);
            return ResponseEntity.ok().body(user);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Return 500 Internal Server Error
        }
    }







    @GetMapping("/order/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable("id") Long id) {
        try {
            return ResponseEntity.ok().body(shopService.findOrderById(id));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Return 500 Internal Server Error
        }
    }

    @PostMapping("/save/order")
    public ResponseEntity<Order> saveOrder(@Valid @RequestBody Order order) {
        try {
            return ResponseEntity.ok().body(shopService.saveOrderToDB(order));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Return 500 Internal Server Error
        }
    }

    @DeleteMapping("/delete/order/{id}")
    public ResponseEntity<Order> deleteOrder(@PathVariable("id") Long id) {
        try {
            Order order = shopService.deleteOrderById(id);
            return ResponseEntity.ok().body(order);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Return 500 Internal Server Error
        }
    }





    @PostMapping("/request/delivery")
    public ResponseEntity<Order> requestDelivery(@Valid @RequestBody Order order) {
        try {
            Order deliveryOrder = shopService.requestDelivery(order);
            return ResponseEntity.ok().body(deliveryOrder);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Return 500 Internal Server Error
        }
    }

    @GetMapping("/request/finish/{id}")
    public ResponseEntity<Order> finishDelivery(@PathVariable("id") Long id) {
        try {
            Order order = shopService.acceptDelivery(id);
            return ResponseEntity.ok().body(order);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Return 500 Internal Server Error
        }
    }

}
