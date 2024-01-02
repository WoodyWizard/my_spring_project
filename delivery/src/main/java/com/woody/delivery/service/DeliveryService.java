package com.woody.delivery.service;

import com.woody.mydata.*;
import com.woody.mydata.token.TokenValidationException;
import jakarta.annotation.PostConstruct;
import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.NoSuchElementException;

@Service
public class DeliveryService {

    @Autowired
    private RestTemplate accessDB;
    private String token = null;
    private HttpHeaders headers = new HttpHeaders();
    private RestTemplate shopRestTemplate;


    @Value("${gdatabase.username}")
    String username;
    @Value("${gdatabase.password}")
    String password;


    public DeliveryService(RestTemplateBuilder restTemplateBuilder) {
        //this.deliveryRepository = deliveryRepository;
        //this.accessDB = restTemplateBuilder.rootUri("http://localhost:8084").build();
        this.shopRestTemplate = restTemplateBuilder.rootUri("http://localhost:8082").build();
    }


    @PostConstruct
    public void checkTokenAfterInit() {
        try {
            CheckToken();
            System.out.println("New token generated : " + token);
        } catch (Exception e) {
            throw new TokenValidationException("Token not generated");
        }
    }

    public Boolean CheckToken() throws Exception {
        if (token == null) {
            if (GenerateToken()) {
                headers.set("Authorization", "Bearer " + token);
                return true;
            } else {
                return false;
            }
        } else {
            if (TokenValidation(token)) {
                headers.set("Authorization", "Bearer " + token);
                return true;
            } else {
                if (GenerateToken()) {
                    headers.set("Authorization", "Bearer " + token);
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



    public Boolean checkDelivery(Long id) {
        Order order = findOrderById(id);
        return order.isDelivered();
    }

    public Boolean checkClientAccept(Long id) {
        Order order = findOrderById(id);
        return order.isClientAccept();
    }

    public Order acceptOrder(Order order) throws Exception {
        if (order != null && order.isValid() && order.isPaid()) {
            order.setStatus("Accept Waiting");
            return order;
        } else {
            throw new OrderValidException();
        }
    }

    public Order finishDelivery(Long id) throws Exception {
            Order order = findOrderById(id);
            order.setDeliverChecker(true);
            order.setStatus("Waiting for user's finish-accept");
            return saveOrderToDB(order);
    }


/*    public String HelloOverRestTemplate() {
        String response = accessDB.getForEntity("/hello", String.class).getBody();
        return response;
    }*/


    public User findUserById(Long id) {
        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity <User> responseUser  = accessDB.exchange("/user/" + id , HttpMethod.GET, request, User.class);
        if (responseUser.getStatusCode() == HttpStatus.OK) {
            return responseUser.getBody();
        } else {
            throw new NoSuchElementException();
        }
    }

/*    public User saveUserToDB(User user) throws Exception {
        HttpEntity<User> request = new HttpEntity<>(user, headers);
        ResponseEntity <User> responseUser  = accessDB.exchange("/save/user" , HttpMethod.POST, request, User.class);
        if (responseUser.getStatusCode() == HttpStatus.OK) {
            return responseUser.getBody();
        } else {
            throw new Exception("Saving of User is failed");
        }
    }

    public User deleteUserById(Long id) {
        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity <User> deletedUser = accessDB.exchange("/delete/user/" + id , HttpMethod.DELETE, request, User.class);
        if (deletedUser.getStatusCode() == HttpStatus.OK) {
            return deletedUser.getBody();
        } else {
            throw new NoSuchElementException();
        }
    }*/












    public Order findOrderById(Long id) {
        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity <Order> responseOrder = accessDB.exchange("/order/" + id , HttpMethod.GET, request, Order.class);
        if (responseOrder.getStatusCode() == HttpStatus.OK) {
            return responseOrder.getBody();
        } else {
            throw new NoSuchElementException();
        }
    }

/*    public Order deleteOrderById(Long id) {
        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity <Order> deletedOrder = accessDB.exchange("/delete/order/" + id , HttpMethod.DELETE, request, Order.class);
        if (deletedOrder.getStatusCode() == HttpStatus.OK) {
            return deletedOrder.getBody();
        } else {
            throw new NoSuchElementException();
        }
    }*/

    public Order saveOrderToDB(Order order) throws Exception {
        HttpEntity<Order> request = new HttpEntity<>(order, headers);
        ResponseEntity <Order> responseOrder = accessDB.exchange("/save/order" , HttpMethod.POST, request, Order.class);
        if (responseOrder.getStatusCode() == HttpStatus.OK) {
            return responseOrder.getBody();
        } else {
            throw new Exception("Saving of Order is Failed");
        }
    }
















/*    public Order syncWithShop(Order order) throws Exception { // it's need to implement retry operation with able to cancel transaction
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
    }*/



    public Deliverer saveDelivererToDB(Deliverer deliverer) {
        HttpEntity<Deliverer> request = new HttpEntity<>(deliverer, headers);
        ResponseEntity <Deliverer> responseDeliverer = accessDB.exchange("/save/deliverer", HttpMethod.POST, request, Deliverer.class);
        if (responseDeliverer.getStatusCode() == HttpStatus.OK) {
            return responseDeliverer.getBody();
        } else {
            throw new NoSuchElementException();
        }
    }

    public Deliverer getDelivererById(Long id) {
        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity <Deliverer> responseDeliverer = accessDB.exchange("/deliverer/" + id, HttpMethod.GET, request, Deliverer.class);
        if (responseDeliverer.getStatusCode() == HttpStatus.OK) {
            return responseDeliverer.getBody();
        } else {
            throw new NoSuchElementException();
        }
    }

    public Deliverer deleteDelivererById(Long id) {
        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity <Deliverer> responseDeliverer = accessDB.exchange("/delete/deliverer/" + id, HttpMethod.DELETE, request, Deliverer.class);
        if (responseDeliverer.getStatusCode() == HttpStatus.OK) {
            return responseDeliverer.getBody();
        } else {
            throw new NoSuchElementException();
        }
    }


}
