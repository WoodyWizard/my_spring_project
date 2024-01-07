package com.woody.shop.service;

import com.woody.mydata.AuthRequest;
import com.woody.mydata.Deliverer;
import com.woody.mydata.Order;
import com.woody.mydata.User;
import com.woody.mydata.menu.OrderItem;
import com.woody.mydata.token.TokenValidationException;
import com.woody.shop.configuration.CustomResponseErrorHandler;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Slf4j
public class ShopService {

    @Autowired
    @Qualifier("DatabaseRest")
    private RestTemplate accessDB;

    @Autowired
    TokenService tokenService;

    private RestTemplate deliveryRestTemplate;



    public ShopService(RestTemplateBuilder restTemplateBuilder) {
        this.deliveryRestTemplate = restTemplateBuilder.rootUri("http://localhost:8083").build();
    }




    public Order findOrderById(Long id) throws Exception {
        HttpEntity<Void> request = new HttpEntity<>(tokenService.getHeaders());
        ResponseEntity <Order> responseOrder = accessDB.exchange("/order/" + id , HttpMethod.GET, request, Order.class);
        if (responseOrder.getStatusCode() == HttpStatus.OK) {
            return responseOrder.getBody();
        } else {
            throw new NoSuchElementException();
        }
    }

    public Order deleteOrderById(Long id) throws Exception {
        HttpEntity<Void> request = new HttpEntity<>(tokenService.getHeaders());
        ResponseEntity <Order> deletedOrder = accessDB.exchange("/delete/order/" + id , HttpMethod.DELETE, request, Order.class);
        if (deletedOrder.getStatusCode() == HttpStatus.OK) {
            return deletedOrder.getBody();
        } else {
            throw new NoSuchElementException();
        }
    }

    public Order saveOrderToDB(Order order) throws Exception {
        HttpEntity<Order> request = new HttpEntity<>(order, tokenService.getHeaders());
        ResponseEntity <Order> responseOrder = accessDB.exchange("/save/order" , HttpMethod.POST, request, Order.class);
        if (responseOrder.getStatusCode() == HttpStatus.OK) {
            return responseOrder.getBody();
        } else {
            throw new Exception("Saving of Order is Failed");
        }
    }








    public User findUserById(Long id) throws Exception {
        HttpEntity<Void> request = new HttpEntity<>(tokenService.getHeaders());
        ResponseEntity <User> responseUser  = accessDB.exchange("/user/" + id , HttpMethod.GET, request, User.class);
        if (responseUser.getStatusCode() == HttpStatus.OK) {
            return responseUser.getBody();
        } else {
            throw new NoSuchElementException();
        }
    }

    public User saveUserToDB(User user) throws Exception {
        HttpEntity<User> request = new HttpEntity<>(user, tokenService.getHeaders());
        ResponseEntity <User> responseUser  = accessDB.exchange("/save/user" , HttpMethod.POST, request, User.class);
        if (responseUser.getStatusCode() == HttpStatus.OK) {
            return responseUser.getBody();
        } else {
            throw new Exception("Saving of User is failed");
        }
    }

    public User deleteUserById(Long id) throws Exception {
        HttpEntity<Void> request = new HttpEntity<>(tokenService.getHeaders());
        ResponseEntity <User> deletedUser = accessDB.exchange("/delete/user/" + id , HttpMethod.DELETE, request, User.class);
        if (deletedUser.getStatusCode() == HttpStatus.OK) {
            return deletedUser.getBody();
        } else {
            throw new NoSuchElementException();
        }
    }











    public Order requestDelivery(Order order) throws Exception {
        if (order.getPaymentStatus() == "Paid") {
            ResponseEntity<Order> response = deliveryRestTemplate.exchange("/request/delivery", HttpMethod.POST, new HttpEntity<>(order, tokenService.getHeaders()), Order.class);
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

            ResponseEntity <Deliverer> response = deliveryRestTemplate.exchange("/save/deliverer", HttpMethod.POST, new HttpEntity<>(deliverer, tokenService.getHeaders()), Deliverer.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                return saveOrderToDB(order);
            } else {
                throw new Exception("Payment error");
            }
        } else {
            throw new Exception("Not Finished");
        }
    }













    public Order requestPayment(Order order) throws Exception {
        if (order.getPaymentStatus() != "Paid" && order.getCustomer().getBalance() >= order.getTotal()) {
            order.getCustomer().setBalance(order.getCustomer().getBalance() - order.getTotal());
            order.setPaymentStatus("Paid");
            ResponseEntity<Order> response = accessDB.exchange("/save/order", HttpMethod.POST, new HttpEntity<>(order, tokenService.getHeaders()), Order.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            } else {
                throw new Exception("Internal server error");
            }
        } else {
            throw new Exception("Balance is not enough");

        }
    }

    public List<OrderItem> getSliderItems() {
        HttpEntity<Void> request = new HttpEntity<>(tokenService.getHeaders());
        ResponseEntity <List<OrderItem>> responseUser  = accessDB.exchange("/slider/4" , HttpMethod.GET, request, new ParameterizedTypeReference<List<OrderItem>>() {});
        if (responseUser.getStatusCode() == HttpStatus.OK) {
            return responseUser.getBody();
        } else {
            throw new NoSuchElementException();
        }

    }


}
