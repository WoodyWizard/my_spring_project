package com.woody.shop.controller;


import com.woody.mydata.AuthRequest;
import com.woody.mydata.Order;
import com.woody.mydata.User;
import com.woody.mydata.menu.OrderItem;
import com.woody.shop.service.ShopService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.ast.Or;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
@Slf4j
public class ShopController {

    private ShopService shopService;



    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody AuthRequest authRequest) {
        try {
            log.info("Login operation started");
            return ResponseEntity.ok().body(shopService.loginRequest(authRequest));
        } catch (NoSuchElementException e) {
            log.error("User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            log.error("Something went wrong");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Return 500 Internal Server Error
        }
    }

    @PostMapping("/verifyToken")
    public ResponseEntity<String> verifyToken(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            log.info("Verify token operation started");
            String token = authorizationHeader.substring(7);
            shopService.verifyToken(token);
            return ResponseEntity.ok().body("Token is valid");
        } catch (NoSuchElementException e) {
            log.error("Token is invalid");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            log.error("Something went wrong");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Return 500 Internal Server Error
        }
    }



    @GetMapping("/user/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") Long id) {
        try {
            log.info("Get user operation started");
            return ResponseEntity.ok().body(shopService.findUserById(id));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Return 500 Internal Server Error
        }
    }

    @GetMapping("/user/user_name/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable("username") String username) {
        try {
            log.info("Get user by USERNAME operation started");
            return ResponseEntity.ok().body(shopService.findUserByUsername(username));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Return 500 Internal Server Error
        }
    }

    @GetMapping("/user/getMe")
    public ResponseEntity<User> getMe(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            log.info("Get ME operation started | authorizationHeader : " + authorizationHeader);
            String token = authorizationHeader.substring(7);
            String username = shopService.extractUsernameFromToken(token);
            return ResponseEntity.ok().body(shopService.findUserByUsername(username));
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
            log.info("Save user operation started");
            return ResponseEntity.ok().body(shopService.saveUserToDB(user));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Return 500 Internal Server Error
        }
    }

    @DeleteMapping("/delete/user/{id}")
    public ResponseEntity<User> deleteUser(@PathVariable("id") Long id) {
        try {
            log.info("Delete user operation started");
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
            log.info("Get order operation started");
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
            log.info("Save order operation started");
            return ResponseEntity.ok().body(shopService.saveOrderToDB(order));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Return 500 Internal Server Error
        }
    }

    @DeleteMapping("/delete/order/{id}")
    public ResponseEntity<Order> deleteOrder(@PathVariable("id") Long id) {
        try {
            log.info("Delete order operation started");
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
            log.info("Request Delivery");
            Order deliveryOrder = shopService.requestDelivery(order);
            return ResponseEntity.ok().body(deliveryOrder);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Return 500 Internal Server Error
        }
    }

    @GetMapping("/request/finish/{id}")
    public ResponseEntity<Order> finishDelivery(@PathVariable("id") Long id) {
        try {
            log.info("Request Finish Delivery");
            Order order = shopService.acceptDelivery(id);
            return ResponseEntity.ok().body(order);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Return 500 Internal Server Error
        }
    }



    @GetMapping("/slider/4")
    public ResponseEntity<List<OrderItem>> getSlider4() {
        try {
            log.info("GET slider operation");
            List<OrderItem> sliderItems = shopService.getSliderItems();
            return ResponseEntity.ok().body(sliderItems);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Return 500 Internal Server Error
        }
    }



    @GetMapping("/cart")
    public ResponseEntity<List<OrderItem>> getCart(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            log.info("GET cart operation");
            String token = authorizationHeader.substring(7);
            String username = shopService.extractUsernameFromToken(token);
            List<OrderItem> cartItems = shopService.getCart(username);
            return ResponseEntity.ok().body(cartItems);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Return 500 Internal Server Error
        }
    }

}
