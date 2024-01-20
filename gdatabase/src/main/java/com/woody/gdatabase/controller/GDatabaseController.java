package com.woody.gdatabase.controller;

import com.woody.gdatabase.serializer.UserSerializer;
import com.woody.gdatabase.service.GDatabaseService;
import com.woody.mydata.*;
import com.woody.mydata.menu.OrderItem;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@EnableMethodSecurity
@CrossOrigin(origins = {"https://localhost:8082" , "http://localhost:5173", "http://localhost:8082"}, allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST})
@Slf4j
public class GDatabaseController {

    private GDatabaseService gDatabaseService;

    private AuthenticationManager authenticationManager;

    private KafkaTemplate<String, Order> kafkaTemplateOrder;
    private KafkaTemplate<String, User> kafkaTemplateUser;

    @GetMapping("/hello")
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("Hello");
    }

    @PreAuthorize("hasAuthority('admin')")
    @GetMapping("/publicKey")
    public ResponseEntity<String> getPublicKey() {
        try {
            log.info("Token Operation (getPublicKey)");
            return ResponseEntity.ok(gDatabaseService.getPublicKey());
        } catch (Exception e) {
            log.error(" /publicKey : Exception");
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/auth/token")
    public ResponseEntity<String> generateToken(@RequestBody AuthRequest authRequest) {
        try {
            log.info("Token Operation (AuthRequest): " , authRequest);
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
            if (authentication.isAuthenticated()) {
                log.info("authentication.isAuthenticated()");
                log.info("Trying to get UserDT");
                UserDT userDT = (UserDT) gDatabaseService.getUserDetailsByUsername(authRequest.getUsername());
                log.info("UserDT : " + userDT);
                log.info("Trying to generateToken");
                return ResponseEntity.ok(gDatabaseService.generateToken(authRequest.getUsername(), userDT));
            } else {
                log.error(" /auth/token : error of authentication");
                return ResponseEntity.badRequest().body("Error of authentication");
            }
        } catch (Exception e) {
            log.error(" /auth/token : Exception : " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/auth/validate")
    public ResponseEntity<String> validateToken(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            log.info("Token validation operation started");
            log.info("Authorization header : " + authorizationHeader);
            String token = authorizationHeader.substring(7);
            log.info("Token extracted : " + token);
            String username = gDatabaseService.extractUsernameFromToken(token);
            log.info("Token username extracted: " + username);
            gDatabaseService.validateToken(token);
            ResponseEntity<String> responseEntity = ResponseEntity.ok().body(token);
            return responseEntity;
        } catch (NoSuchElementException e) {
            log.error("Token validation isn't successful * : " + authorizationHeader.substring(7));
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Token validation isn't successful * : " + authorizationHeader.substring(7) + " : " + e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasAuthority('admin')")
    @PostMapping("/token/username")
    public ResponseEntity<String> getUsernameFromToken(@RequestBody String token) {
        try {
            log.info("Token username extraction operation started");
            String username = gDatabaseService.extractUsernameFromToken(token);
            log.info("Token extracted");
            return ResponseEntity.ok().body(username);
        } catch (Exception e) {
            log.error("Token username extraction isn't successful * : ", token);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
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

    public void sendUserToKafka(User user) {
        log.info("User sending to Kafka");
        kafkaTemplateUser.send("user", user);
        log.info("User sent to Kafka");
    }

    @PreAuthorize("hasAuthority('admin')")
    @GetMapping("/user/{id}")
    public ResponseEntity<User> findUserById(@PathVariable("id") Long id) {
        try {
            //UserSerializer  userSerializer = new UserSerializer();
            log.info("User getting by id operation started | id : " + id);
            User user = gDatabaseService.getUserById(id);
            return ResponseEntity.ok(user);
        } catch (NoSuchElementException e) {
            log.error("User are not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            log.error("User getting operation EXCEPTION");
            return ResponseEntity.internalServerError().build();
        }
    }

    @PreAuthorize("hasAuthority('admin')")
    @GetMapping("/user/user_name/{username}")
    public ResponseEntity<User> findUserByUsername(@PathVariable("username") String username) {
        try {
            UserSerializer  userSerializer = new UserSerializer();
            log.info("User getting operation started");
            User user = gDatabaseService.getUserByUsername(username);
            //log.info("Serialize user : " + userSerializer.serializeToByteArray(user));
            sendUserToKafka(user);
            return ResponseEntity.ok(user);
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










    @PreAuthorize("hasAuthority('admin')")
    @PostMapping("/cart/add")
    public ResponseEntity<OrderItem> addToCart(@RequestBody Map<String, String> requestBody) {
        try {
            log.info("OrderItem saving operation started");
            String username = requestBody.get("username");
            log.info(username);
            OrderItem orderItem = gDatabaseService.getOrderItemById(Long.valueOf(requestBody.get("itemId")));
            log.info("Item is presented in database: " + orderItem.toString());
            log.info("OrderItem saving operation started");
            gDatabaseService.addItemToCart(username, orderItem);
            log.info("OrderItem saving operation finished");
            return ResponseEntity.ok().body(orderItem);
        } catch (Exception e) {
            log.error("OrderItem saving operation failed");
            return ResponseEntity.internalServerError().build();
        }

    }


    @PreAuthorize("hasAuthority('admin')")
    @GetMapping("/cart/{username}")
    public ResponseEntity<List<OrderItem>> getCart(@PathVariable("username") String username) {
        try {
            log.info("Get cart started");
            return ResponseEntity.ok(gDatabaseService.getCart(username));
        } catch (NoSuchElementException e) {
            log.error("Cart is not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            log.error("CART EXCEPTION");
            return ResponseEntity.internalServerError().build();
        }
    }

    @PreAuthorize("hasAuthority('admin')")
    @DeleteMapping("/cart/delete")
    public ResponseEntity<String> deleteFromCart(@RequestBody Map<String, Object> requestBody) {
        try {
            log.info("OrderItem deleting operation started");
            String username = (String) requestBody.get("username");
            OrderItem orderItem = (OrderItem) requestBody.get("orderItem");

            gDatabaseService.deleteItemFromCart(username, orderItem);
            return ResponseEntity.ok().body("OK");
        } catch (Exception e) {
            log.error("OrderItem deleting operation failed");
            return ResponseEntity.internalServerError().build();
        }
    }


}
