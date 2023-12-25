package com.woody.gdatabase.controller;

import com.woody.gdatabase.service.GDatabaseService;
import com.woody.mydata.AuthRequest;
import com.woody.mydata.Deliverer;
import com.woody.mydata.Order;
import com.woody.mydata.User;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor

public class GDatabaseController {

    private GDatabaseService gDatabaseService;

    private AuthenticationManager authenticationManager;

    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }

    @PostMapping("/auth/token")
    public ResponseEntity<String> generateToken(@RequestBody AuthRequest authRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
            if (authentication.isAuthenticated()) {
                return ResponseEntity.ok(gDatabaseService.generateToken(authRequest.getUsername()));
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/auth/validate")
    public ResponseEntity<String> validateToken(@RequestParam("token") String token) {
        try {
            gDatabaseService.validateToken(token);
            return ResponseEntity.ok("Validation");
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/save/order")
    public ResponseEntity<Order> saveOrder(@RequestBody Order order) {
        try {
            return ResponseEntity.ok(gDatabaseService.saveOrder(order));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/order/{id}")
    public ResponseEntity<Order> findOrderById(@PathVariable("id") Long id) {
        try {
            return ResponseEntity.ok(gDatabaseService.findOrderById(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }


    @PostMapping("/save/user")
    public ResponseEntity<User> saveUser(@RequestBody User user) {
        try {
            return ResponseEntity.ok(gDatabaseService.saveUser(user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<User> findUserById(@PathVariable("id") Long id) {
        try {
            return ResponseEntity.ok(gDatabaseService.getUserById(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }


    @PostMapping("/save/deliverer")
    public ResponseEntity<Deliverer> saveDeliverer(@RequestBody Deliverer deliverer) {
        try {
            return ResponseEntity.ok(gDatabaseService.saveDeliverer(deliverer));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/deliverer/{id}")
    public ResponseEntity<Deliverer> findDelivererById(@PathVariable("id") Long id) {
        try {
            return ResponseEntity.ok(gDatabaseService.getDelivererById(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/delete/order/{id}")
    public ResponseEntity<Order> deleteOrder(@PathVariable("id") Long id) {
        try {
            return ResponseEntity.ok(gDatabaseService.deleteOrder(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }



}
