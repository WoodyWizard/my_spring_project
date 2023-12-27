package com.woody.delivery.service;

import com.woody.delivery.repository.DeliveryRepository;
import com.woody.mydata.*;
import com.woody.mydata.token.TokenValidationException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.NoSuchElementException;

@Service
public class DeliveryService {

    //private DeliveryRepository deliveryRepository;

    @Autowired
    private RestTemplate accessDB;

    private String token = null;
    private HttpHeaders headers = new HttpHeaders();

    private RestTemplate gdatabaseRestTemplate;
    private RestTemplate shopRestTemplate;


    @Value("${gdatabase.username}")
    String username;
    @Value("${gdatabase.password}")
    String password;


    @PostConstruct
    public void checkTokenAfterInit() {
        try {
            CheckToken();
            System.out.println("New token generated : " + token);
            headers.set("Authorization", "Bearer " + token);
        } catch (Exception e) {
            throw new TokenValidationException("Token not generated");
        }
    }

    public Boolean CheckToken() throws Exception {
        if (token == null) {
            if (GenerateToken()) {
                return true;
            } else {
                return false;
            }
        } else {
            if (TokenValidation(token)) {
                return true;
            } else {
                if (GenerateToken()) {
                    return true;
                } else {
                    return false;
                }
            }
        }
    }

    public Boolean TokenValidation(String token_to_check) {
        ResponseEntity<String> response = accessDB.getForEntity("/auth/validate", String.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean GenerateToken() throws Exception {
        if (username == null || password == null) {
            throw new Exception("Username or password not set");
        }
        AuthRequest authRequest = new AuthRequest(username, password);
        ResponseEntity<String> response = accessDB.postForEntity("/auth/token", authRequest, String.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            token = response.getBody();
            return true;
        } else {
            token = null;
            throw new Exception("Token not generated");
        }
    }


    public User findUserById(Long id) {
        HttpEntity<User> request = new HttpEntity<>(headers);
        User responseUser = accessDB.exchange("/user/" + id , HttpMethod.GET, request, User.class, id).getBody();
        if (responseUser != null) {
            return responseUser;
        } else {
            throw new NoSuchElementException();
        }
    }

    public String HelloOverRestTemplate() {
        String response = accessDB.getForEntity("/hello", String.class).getBody();
        return response;
    }

    public DeliveryService(DeliveryRepository deliveryRepository, RestTemplateBuilder restTemplateBuilder) {
        //this.deliveryRepository = deliveryRepository;
        //this.accessDB = restTemplateBuilder.rootUri("http://localhost:8084").build();
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
