package com.woody.delivery.service;

import com.woody.delivery.repository.DeliveryRepository;
import com.woody.mydata.Deliverer;
import com.woody.mydata.Order;
import com.woody.mydata.OrderValidException;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Service

public class DeliveryService {

    //private DeliveryRepository deliveryRepository;
    private RestTemplate accessDB;
    private RestTemplate shopRestTemplate;


    public DeliveryService(DeliveryRepository deliveryRepository, RestTemplateBuilder restTemplateBuilder) {
        //this.deliveryRepository = deliveryRepository;
        this.accessDB = restTemplateBuilder.rootUri("http://localhost:8084").build();
        this.shopRestTemplate = restTemplateBuilder.rootUri("http://localhost:8082").build();
    }

    public Order findOrderById(Long id) {
        Order responseOrder = accessDB.getForEntity("/order/" + id + "/", Order.class).getBody();
        if (responseOrder != null) {
            return responseOrder;
        } else {
            throw new NoSuchElementException();
        }
    }


    public Order acceptOrder(Order order) throws Exception {
        if (order != null && order.isValid() && order.isPaid()) {
            order.setStatus("Accept Waiting");
            syncWithShop(order);
            //saveOrderToDB(order);
            //deliveryRepository.save(order);
            return order;
        } else {
            throw new OrderValidException();
        }
    }


    public Order deleteOrder(Long id) {
        ResponseEntity <Order> deletedOrder = accessDB.getForEntity("/delete/oder/" + id + "/", Order.class);
        if (deletedOrder.getStatusCode() == HttpStatus.OK) {
            return deletedOrder.getBody();
        } else {
            throw new NoSuchElementException();
        }
    }

    public Boolean checkDelivery(Long id) {
        Order order = findOrderById(id);
        return order.isDelivered();
    }

    public Boolean checkClientAccept(Long id) {
        Order order = findOrderById(id);
        return order.isClientAccept();
    }

    public Order finishDelivery(Long id) throws Exception {
        Order order = findOrderById(id);
        if (order.getDeliverChecker() && order.getClientChecker()) {
            order.setStatus("Finished");
            return saveOrderToDB(order);
        } else {
            throw new Exception("Not Finished");
        }
    }

    public Order saveOrderToDB(Order order) throws Exception {
        Order responseOrder = accessDB.postForEntity("/save/order", order, Order.class).getBody();
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

    public Deliverer saveDelivererToDB(Deliverer deliverer) {
        Deliverer responseDeliverer = accessDB.postForEntity("/save/deliverer", deliverer, Deliverer.class).getBody();
        if (responseDeliverer != null) {
            return responseDeliverer;
        } else {
            throw new NoSuchElementException();
        }
    }
}
