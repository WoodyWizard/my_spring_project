package com.woody.gdatabase.service;

import com.woody.gdatabase.repository.DelivererRepository;
import com.woody.gdatabase.repository.OrderRepository;
import com.woody.gdatabase.repository.UserRepository;
import com.woody.gdatabase.security.service.JWTService;
import com.woody.mydata.Deliverer;
import com.woody.mydata.Order;
import com.woody.mydata.User;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@AllArgsConstructor

public class GDatabaseService {

    private OrderRepository gDatabaseRepository;
    private UserRepository userRepository;
    private DelivererRepository delivererRepository;
    private JWTService jwtService;


    public Order saveOrder(Order order) {
        return gDatabaseRepository.save(order);
    }

    public Order findOrderById(Long id) {
        return gDatabaseRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public Deliverer saveDeliverer(Deliverer deliverer) {
        return delivererRepository.save(deliverer);
    }

    public Deliverer getDelivererById(Long id) {
        return delivererRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public Order deleteOrder(Long id) {
        Optional<Order> orderOptional = gDatabaseRepository.findById(id);
        if (orderOptional.isPresent()) {
            gDatabaseRepository.deleteById(id);
            return orderOptional.get();
        } else {
            throw new NoSuchElementException();
        }
    }


    public String generateToken(String username) {
        return jwtService.generateToken(new HashMap<>() , username);
    }

    public void validateToken(String token) {
        jwtService.validateToken(token);
    }
}
