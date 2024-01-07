package com.woody.gdatabase.controller;

import com.woody.gdatabase.service.GDatabaseService;
import com.woody.mydata.AuthRequest;
import com.woody.mydata.Deliverer;
import com.woody.mydata.Order;
import com.woody.mydata.User;
import com.woody.mydata.menu.OrderItem;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@AllArgsConstructor
@EnableMethodSecurity
@Slf4j
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
            log.info("Token Operation (AuthRequest): " , authRequest);
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
            if (authentication.isAuthenticated()) {
                log.info("Successful operation of generating token");
                return ResponseEntity.ok(gDatabaseService.generateToken(authRequest.getUsername()));
            } else {
                log.error(" /auth/token : error of authentication");
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            log.error(" /auth/token : Exception");
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/auth/validate")
    public ResponseEntity<String> validateToken(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            log.info("Token validation operation started");
            String token = authorizationHeader.substring(7);
            gDatabaseService.validateToken(token);
            log.info("Token validation successful");
            return ResponseEntity.ok("Valid");
        } catch (Exception e) {
            log.error("Token validation isn't successful * : ", authorizationHeader.substring(7));
            return ResponseEntity.notFound().build();
        }
    }





    @PreAuthorize("hasAuthority('admin')")
    @PostMapping("/save/user")
    public ResponseEntity<User> saveUser(@RequestBody User user) {
        try {
            log.info("User saving operation started");
            return ResponseEntity.ok(gDatabaseService.saveUser(user));
        } catch (Exception e) {
            log.error("User saving operation failed");
            return ResponseEntity.internalServerError().build();
        }
    }

    @PreAuthorize("hasAuthority('admin')")
    @GetMapping("/user/{id}")
    public ResponseEntity<User> findUserById(@PathVariable("id") Long id) {
        try {
            log.info("User getting operation started");
            return ResponseEntity.ok(gDatabaseService.getUserById(id));
        } catch (NoSuchElementException e) {
            log.error("User are not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            log.error("User getting operation EXCEPTION");
            return ResponseEntity.internalServerError().build();
        }
    }

    @PreAuthorize("hasAuthority('admin')")
    @DeleteMapping("/delete/user/{id}")
    public ResponseEntity<User> deleteUser(@PathVariable("id") Long id) {
        try {
            log.info("User delete operation started");
            User user = gDatabaseService.deleteUserById(id);
            return ResponseEntity.ok().body(user);
        } catch (NoSuchElementException e) {
            log.error("User are not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            log.error("User delete operation EXCEPTION");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Return 500 Internal Server Error
        }
    }









    @PreAuthorize("hasAuthority('admin')")
    @PostMapping("/save/deliverer")
    public ResponseEntity<Deliverer> saveDeliverer(@RequestBody Deliverer deliverer) {
        try {
            log.info("Deliverer saving operation started");
            return ResponseEntity.ok(gDatabaseService.saveDeliverer(deliverer));
        } catch (Exception e) {
            log.error("Deliverer saving operation failed");
            return ResponseEntity.internalServerError().build();
        }
    }
    @PreAuthorize("hasAuthority('admin')")
    @GetMapping("/deliverer/{id}")
    public ResponseEntity<Deliverer> findDelivererById(@PathVariable("id") Long id) {
        try {
            log.info("Deliverer getting operation started");
            return ResponseEntity.ok(gDatabaseService.getDelivererById(id));
        } catch (NoSuchElementException e) {
            log.error("Deliverer are not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            log.error("Deliverer getting operation failed EXCEPTION");
            return ResponseEntity.internalServerError().build();
        }
    }

    @PreAuthorize("hasAuthority('admin')")
    @DeleteMapping("/delete/deliverer/{id}")
    public ResponseEntity<Deliverer> deleteDeliverer(@PathVariable("id") Long id) {
        try {
            log.info("Deliverer delete operation started");
            return ResponseEntity.ok(gDatabaseService.deleteDelivererById(id));
        } catch (NoSuchElementException e) {
            log.error("Deliverer are not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            log.error("Deliverer delete operation failed EXCEPTION");
            return ResponseEntity.internalServerError().build();
        }
    }






    @PreAuthorize("hasAuthority('admin')")
    @PostMapping("/save/order")
    public ResponseEntity<Order> saveOrder(@RequestBody Order order) {
        try {
            log.info("Order saving operation started");
            return ResponseEntity.ok(gDatabaseService.saveOrder(order));
        } catch (Exception e) {
            log.error("Order saving operation failed");
            return ResponseEntity.internalServerError().build();
        }
    }
    @PreAuthorize("hasAuthority('admin')")
    @GetMapping("/order/{id}")
    public ResponseEntity<Order> findOrderById(@PathVariable("id") Long id) {
        try {
            log.info("Order getting operation started");
            return ResponseEntity.ok(gDatabaseService.findOrderById(id));
        } catch (NoSuchElementException e) {
            log.error("Order are not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            log.error("Order getting operation failed EXCEPTION");
            return ResponseEntity.internalServerError().build();
        }
    }
    @PreAuthorize("hasAuthority('admin')")
    @DeleteMapping("/delete/order/{id}")
    public ResponseEntity<Order> deleteOrder(@PathVariable("id") Long id) {
        try {
            log.info("Order delete operation started");
            return ResponseEntity.ok(gDatabaseService.deleteOrder(id));
        } catch (NoSuchElementException e) {
            log.error("Order are not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            log.error("Order delete operation failed EXCEPTION");
            return ResponseEntity.internalServerError().build();
        }
    }









    @PreAuthorize("hasAuthority('admin')")
    @GetMapping("/slider/4")
    public ResponseEntity<List<OrderItem>> getSliderItems() {
        try {
            log.info("Get slider started");
            return ResponseEntity.ok(gDatabaseService.getRecordsForSlider());
        } catch (NoSuchElementException e) {
            log.error("Orders for slider is not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            log.error("SLIDER EXCEPTION");
            return ResponseEntity.internalServerError().build();
        }
    }

}
