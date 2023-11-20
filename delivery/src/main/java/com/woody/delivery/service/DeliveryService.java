package com.woody.delivery.service;

import com.woody.delivery.repository.DeliveryRepository;
import com.woody.mydata.Order;
import com.woody.mydata.OrderValidException;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Service

public class DeliveryService {

    private DeliveryRepository deliveryRepository;
    private RestTemplate restTemplate;
    private RestTemplate shopRestTemplate;


    public DeliveryService(DeliveryRepository deliveryRepository, RestTemplateBuilder restTemplateBuilder) {
        this.deliveryRepository = deliveryRepository;
        this.restTemplate = restTemplateBuilder.rootUri("http://localhost:8084").build();
        this.shopRestTemplate = restTemplateBuilder.rootUri("http://localhost:8082").build();
    }


    public Order acceptOrder(Order order) {
        if (order != null && order.isValid() == true) {
            order.setStatus("Accept Waiting");
            deliveryRepository.save(order);
            return order;
        } else {
            throw new OrderValidException();
        }
    }

    public Order foundOrder(Long id) {
        Optional<Order> orderOptional = deliveryRepository.findById(id);
        return orderOptional.orElseThrow(() -> new NoSuchElementException());
    }

    public Order deleteOrder(Long id) {
        Optional<Order> orderOptional = deliveryRepository.findById(id);
        if (orderOptional.isPresent()) {
            deliveryRepository.deleteById(id);
            return orderOptional.get();
        } else {
            throw new NoSuchElementException();
        }
    }

    public Boolean checkDelivery(Long id) {
        Order order = deliveryRepository.findById(id).get();
        return order.isDelivered();
    }

    public Boolean checkClientAccept(Long id) {
        Order order = deliveryRepository.findById(id).get();
        return order.isClientAccept();
    }

    public Order finishDelivery(Long id) throws Exception {
        Order order = deliveryRepository.findById(id).get();
        if (order.getDeliverChecker() == true && order.getClientChecker() == true) {
            order.setStatus("Finished");
            return deliveryRepository.save(order);
        } else {
            throw new Exception("Not Finished");
        }
    }

    public Order saveToGDatabase(Order order) throws Exception {
        Order responseOrder = restTemplate.postForEntity("/save", order, Order.class).getBody();
        if (responseOrder != null) {
            return responseOrder;
        } else {
            throw new Exception("Save Failed");
        }
    }

    public Order syncWithShop(Order order) throws Exception { // it's need to implement retry operation with able to cancel transaction
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity <Order> request = new HttpEntity<>(order, headers);
        ResponseEntity <Order> responseOrder = shopRestTemplate.exchange("/order/{id}", HttpMethod.PUT, request, Order.class, order.getId());
        if (responseOrder.getStatusCode().is2xxSuccessful()) {
            if (responseOrder.getBody() != null) {
                return responseOrder.getBody();
            } else {
                throw new Exception("Sync Failed");
            }
        }
        throw new Exception("Sync Failed");
    }
}
