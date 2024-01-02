package com.woody.shop.service;

import com.woody.mydata.AuthRequest;
import com.woody.mydata.Deliverer;
import com.woody.mydata.Order;
import com.woody.mydata.User;
import com.woody.mydata.token.TokenValidationException;
import jakarta.annotation.PostConstruct;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.NoSuchElementException;

@Service
public class ShopService {

    @Autowired
    private RestTemplate accessDB;

    private String token = null;
    private HttpHeaders headers = new HttpHeaders();
    private RestTemplate deliveryRestTemplate;

    @Value("${gdatabase.username}")
    String username;
    @Value("${gdatabase.password}")
    String password;


    public ShopService(RestTemplateBuilder restTemplateBuilder) {
        this.deliveryRestTemplate = restTemplateBuilder.rootUri("http://localhost:8083").build();
    }

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















    public Order findOrderById(Long id) throws Exception {
        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity <Order> responseOrder = accessDB.exchange("/order/" + id , HttpMethod.GET, request, Order.class);
        if (responseOrder.getStatusCode() == HttpStatus.OK) {
            return responseOrder.getBody();
        } else {
            throw new NoSuchElementException();
        }
    }

    public Order deleteOrderById(Long id) throws Exception {
        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity <Order> deletedOrder = accessDB.exchange("/delete/order/" + id , HttpMethod.DELETE, request, Order.class);
        if (deletedOrder.getStatusCode() == HttpStatus.OK) {
            return deletedOrder.getBody();
        } else {
            throw new NoSuchElementException();
        }
    }

    public Order saveOrderToDB(Order order) throws Exception {
        HttpEntity<Order> request = new HttpEntity<>(order, headers);
        ResponseEntity <Order> responseOrder = accessDB.exchange("/save/order" , HttpMethod.POST, request, Order.class);
        if (responseOrder.getStatusCode() == HttpStatus.OK) {
            return responseOrder.getBody();
        } else {
            throw new Exception("Saving of Order is Failed");
        }
    }








    public User findUserById(Long id) throws Exception {
        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity <User> responseUser  = accessDB.exchange("/user/" + id , HttpMethod.GET, request, User.class);
        if (responseUser.getStatusCode() == HttpStatus.OK) {
            return responseUser.getBody();
        } else {
            throw new NoSuchElementException();
        }
    }

    public User saveUserToDB(User user) throws Exception {
        HttpEntity<User> request = new HttpEntity<>(user, headers);
        ResponseEntity <User> responseUser  = accessDB.exchange("/save/user" , HttpMethod.POST, request, User.class);
        if (responseUser.getStatusCode() == HttpStatus.OK) {
            return responseUser.getBody();
        } else {
            throw new Exception("Saving of User is failed");
        }
    }

    public User deleteUserById(Long id) throws Exception {
        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity <User> deletedUser = accessDB.exchange("/delete/user/" + id , HttpMethod.DELETE, request, User.class);
        if (deletedUser.getStatusCode() == HttpStatus.OK) {
            return deletedUser.getBody();
        } else {
            throw new NoSuchElementException();
        }
    }





    public Order requestPayment(Order order) throws Exception {
        if (order.getPaymentStatus() != "Paid" && order.getCustomer().getBalance() >= order.getTotal()) {
            order.getCustomer().setBalance(order.getCustomer().getBalance() - order.getTotal());
            order.setPaymentStatus("Paid");
            ResponseEntity<Order> response = accessDB.exchange("/save/order", HttpMethod.POST, new HttpEntity<>(order, headers), Order.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            } else {
                throw new Exception("Internal server error");
            }
        } else {
            throw new Exception("Balance is not enough");

        }
    }





    public Order requestDelivery(Order order) throws Exception {
        if (order.getPaymentStatus() == "Paid") {
            ResponseEntity<Order> response = deliveryRestTemplate.exchange("/request/delivery", HttpMethod.POST, new HttpEntity<>(order, headers), Order.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            } else {
                throw new NoSuchElementException();
            }
        } else {
            throw new Exception("Waiting for Payment");
        }
    }

    public Order acceptDelivery(Long id) throws Exception {
        Order order = findOrderById(id);
        if (order.getDeliverChecker()) {
            order.setClientChecker(true);
            order.setStatus("Finished");

            Deliverer deliverer = order.getDeliverer();
            deliverer.setBalance(deliverer.getBalance() + order.getShipping());

            ResponseEntity <Deliverer> response = deliveryRestTemplate.exchange("/save/deliverer", HttpMethod.POST, new HttpEntity<>(deliverer, headers), Deliverer.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                return saveOrderToDB(order);
            } else {
                throw new Exception("Payment error");
            }
        } else {
            throw new Exception("Not Finished");
        }
    }




}
