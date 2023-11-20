package com.woody.gdatabase.service;

import com.woody.gdatabase.repository.OrderRepository;
import com.woody.mydata.Order;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor

public class GDatabaseService {

    private OrderRepository gDatabaseRepository;


    public Order saveOrder(Order order) {
        return gDatabaseRepository.save(order);
    }

    public Order findOrderById(Long id) {
        return gDatabaseRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
    }
}
