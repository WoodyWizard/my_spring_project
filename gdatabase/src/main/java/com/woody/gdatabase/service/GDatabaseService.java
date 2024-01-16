package com.woody.gdatabase.service;

import com.woody.gdatabase.repository.DelivererRepository;
import com.woody.gdatabase.repository.ItemRepository;
import com.woody.gdatabase.repository.OrderRepository;
import com.woody.gdatabase.repository.UserRepository;
import com.woody.gdatabase.security.service.CustomUserDetailsService;
import com.woody.gdatabase.security.service.JWTService;
import com.woody.mydata.Deliverer;
import com.woody.mydata.Order;
import com.woody.mydata.User;
import com.woody.mydata.UserDT;
import com.woody.mydata.menu.OrderItem;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class GDatabaseService {

    private OrderRepository gDatabaseRepository;
    private UserRepository userRepository;
    private DelivererRepository delivererRepository;
    private ItemRepository itemRepository;
    private JWTService jwtService;
    private CustomUserDetailsService customUserDetailsService;









    public UserDT getUserDetailsByUsername(String username) {
        return (UserDT) customUserDetailsService.loadUserByUsername(username);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }
    public User getUserById(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            log.info("Successful operation of getting user");
            return userOptional.get();
        } else {
            log.error("Error: Get user from database");
            throw new NoSuchElementException();
        }
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
    }
    public User deleteUserById(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            userRepository.deleteById(id);
            log.info("Successful Operation of deleting User from Database");
            return userOptional.get();
        } else {
            log.error("Error: Delete user from database");
            throw new NoSuchElementException();
        }
    }








    public Deliverer saveDeliverer(Deliverer deliverer) {
        return delivererRepository.save(deliverer);
    }
    public Deliverer getDelivererById(Long id) {
        return delivererRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }
    public Deliverer deleteDelivererById(Long id) {
        Optional<Deliverer> delivererOptional = delivererRepository.findById(id);
        if (delivererOptional.isPresent()) {
            delivererRepository.deleteById(id);
            log.info("Successful Operation of deleting deliverer from database");
            return delivererOptional.get();
        } else {
            log.error("Error: Delete deliverer from database");
            throw new NoSuchElementException();
        }
    }





    public Order findOrderById(Long id) {
        Optional<Order> orderOptional = gDatabaseRepository.findById(id);
        if (orderOptional.isPresent()) {
            log.info("Order has been found");
            return orderOptional.get();
        } else {
            log.error("Error: Get Order from database");
            throw new NoSuchElementException();
        }
    }
    public Order saveOrder(Order order) {
        return gDatabaseRepository.save(order);
    }
    public Order deleteOrder(Long id) {
        Optional<Order> orderOptional = gDatabaseRepository.findById(id);
        if (orderOptional.isPresent()) {
            gDatabaseRepository.deleteById(id);
            log.info("Successful operation of Deleting order from database");
            return orderOptional.get();
        } else {
            log.error("Error: Delete Order from database");
            throw new NoSuchElementException();
        }
    }








    public String generateToken(String username, UserDT userDT) throws Exception {
        log.info("Generating token");
        return jwtService.generateToken(new HashMap<>() , username, userDT.getAuthorities());
    }

    public void validateToken(String token) throws Exception {
        jwtService.validateToken(token);
    }

    public String extractUsernameFromToken(String token) {
        return jwtService.extractUsername(token);
    }

    public String getPublicKey() {
        String publicKey = jwtService.getPublicKey();
        if (publicKey != null) {
            log.info("Successful operation of getting public key");
            return publicKey;
        } else {
            log.error("Error: Get public key from database");
            throw new NoSuchElementException();
        }
    }











    public List<OrderItem> getRecordsForSlider() {
        List <OrderItem> orderItems = itemRepository.findRandomFourEntities();
        System.out.println(orderItems);
        if (orderItems.size() < 4 && orderItems.isEmpty()) {
            log.error("Error: get 4 entities for slider (from database)");
            throw new NoSuchElementException();
        }
        log.info("Returning 4 entities from database");
        return orderItems;
    }





    public void addItemToCart(String username ,OrderItem orderItem) {
        User user = this.getUserByUsername(username);
        user.getCart().add(orderItem);
        this.saveUser(user);
    }

    public void deleteItemFromCart(String username ,OrderItem orderItem) {
        User user = this.getUserByUsername(username);
        user.getCart().remove(orderItem);
        this.saveUser(user);
    }

    public List<OrderItem> getCart(String username) {
        User user = this.getUserByUsername(username);
        return user.getCart();
    }


    public OrderItem getOrderItemById(Long itemId) {
        Optional<OrderItem> orderItemOptional = itemRepository.findById(itemId);
        if (orderItemOptional.isPresent()) {
            log.info("OrderItem has been found");
            return orderItemOptional.get();
        } else {
            log.error("Error: Get OrderItem from database");
            throw new NoSuchElementException();
        }
    }
}
