package com.woody.gdatabase.controller;

import com.woody.gdatabase.service.GDatabaseService;
import com.woody.mydata.AuthRequest;
import com.woody.mydata.Deliverer;
import com.woody.mydata.Order;
import com.woody.mydata.User;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@AllArgsConstructor
@EnableMethodSecurity
public class GDatabaseController {

    private GDatabaseService gDatabaseService;

    private AuthenticationManager authenticationManager;

    @GetMapping("/hello")
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("Hello");
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
            return ResponseEntity.ok("Valid");
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }





    @PreAuthorize("hasAuthority('admin')")
    @PostMapping("/save/user")
    public ResponseEntity<User> saveUser(@RequestBody User user) {
        try {
            return ResponseEntity.ok(gDatabaseService.saveUser(user));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PreAuthorize("hasAuthority('admin')")
    @GetMapping("/user/{id}")
    public ResponseEntity<User> findUserById(@PathVariable("id") Long id) {
        try {
            return ResponseEntity.ok(gDatabaseService.getUserById(id));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PreAuthorize("hasAuthority('admin')")
    @DeleteMapping("/delete/user/{id}")
    public ResponseEntity<User> deleteUser(@PathVariable("id") Long id) {
        try {
            User user = gDatabaseService.deleteUserById(id);
            return ResponseEntity.ok().body(user);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Return 500 Internal Server Error
        }
    }









    @PreAuthorize("hasAuthority('admin')")
    @PostMapping("/save/deliverer")
    public ResponseEntity<Deliverer> saveDeliverer(@RequestBody Deliverer deliverer) {
        try {
            return ResponseEntity.ok(gDatabaseService.saveDeliverer(deliverer));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    @PreAuthorize("hasAuthority('admin')")
    @GetMapping("/deliverer/{id}")
    public ResponseEntity<Deliverer> findDelivererById(@PathVariable("id") Long id) {
        try {
            return ResponseEntity.ok(gDatabaseService.getDelivererById(id));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PreAuthorize("hasAuthority('admin')")
    @DeleteMapping("/delete/deliverer/{id}")
    public ResponseEntity<Deliverer> deleteDeliverer(@PathVariable("id") Long id) {
        try {
            return ResponseEntity.ok(gDatabaseService.deleteDelivererById(id));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }






    @PreAuthorize("hasAuthority('admin')")
    @PostMapping("/save/order")
    public ResponseEntity<Order> saveOrder(@RequestBody Order order) {
        try {
            return ResponseEntity.ok(gDatabaseService.saveOrder(order));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    @PreAuthorize("hasAuthority('admin')")
    @GetMapping("/order/{id}")
    public ResponseEntity<Order> findOrderById(@PathVariable("id") Long id) {
        try {
            return ResponseEntity.ok(gDatabaseService.findOrderById(id));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    @PreAuthorize("hasAuthority('admin')")
    @DeleteMapping("/delete/order/{id}")
    public ResponseEntity<Order> deleteOrder(@PathVariable("id") Long id) {
        try {
            return ResponseEntity.ok(gDatabaseService.deleteOrder(id));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }



}
