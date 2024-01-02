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
import org.springframework.transaction.annotation.Transactional;

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











    public User saveUser(User user) {
        return userRepository.save(user);
    }
    public User getUserById(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            return userOptional.get();
        } else {
            throw new NoSuchElementException();
        }
    }
    public User deleteUserById(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            userRepository.deleteById(id);
            return userOptional.get();
        } else {
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
            return delivererOptional.get();
        } else {
            throw new NoSuchElementException();
        }
    }





    public Order findOrderById(Long id) {
        Optional<Order> orderOptional = gDatabaseRepository.findById(id);
        if (orderOptional.isPresent()) {
            return orderOptional.get();
        } else {
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
