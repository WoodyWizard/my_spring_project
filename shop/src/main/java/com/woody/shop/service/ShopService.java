package com.woody.shop.service;

import com.woody.mydata.*;
import com.woody.mydata.menu.OrderItem;
import com.woody.mydata.token.TokenValidationException;
import com.woody.shop.configuration.CustomResponseErrorHandler;
import io.jsonwebtoken.io.Decoders;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.security.Key;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public User findUserByUsername(String username) throws Exception {
        log.info("Find user by username operation started");
        HttpEntity<Void> request = new HttpEntity<>(tokenService.getHeaders());
        ResponseEntity <User> responseUser  = accessDB.exchange("/user/user_name/" + username , HttpMethod.GET, request, User.class);
        if (responseUser.getStatusCode() == HttpStatus.OK) {
            log.info("User by username founded");
            return responseUser.getBody();
        } else {
            log.error("User by username not founded");
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


    public String loginRequest(AuthRequest authRequest) {
        HttpEntity<AuthRequest> request = new HttpEntity<>(authRequest, tokenService.getHeaders());
        ResponseEntity <String> responseToken  = accessDB.exchange("/auth/token" , HttpMethod.POST, request, String.class);
        if (responseToken.getStatusCode() == HttpStatus.OK) {
            //System.out.println("token new: " + responseToken.getBody());
            return responseToken.getBody();
        } else {
            throw new NoSuchElementException();
        }
    }



    private PublicKey convertStringToPublicKey(String publicKeyAsString, String algorithm) throws Exception {
        byte [] publicKeyAsBytes = Decoders.BASE64.decode(publicKeyAsString);
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
        X509EncodedKeySpec x509EncodedKeySpec=new X509EncodedKeySpec(publicKeyAsBytes);
        log.info("Public key: {}", x509EncodedKeySpec);
        return keyFactory.generatePublic(x509EncodedKeySpec);
    }

    public Key getPublicKey() throws Exception {
        HttpEntity<Void> request = new HttpEntity<>(tokenService.getHeaders());
        ResponseEntity <String> response  = accessDB.exchange("/publicKey" , HttpMethod.GET, request, String.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            String publicKeyString = response.getBody();
            PublicKey publicKey = convertStringToPublicKey(publicKeyString, "RSA");
            tokenService.setPublicKey(publicKey);
            return publicKey;
        } else {
            throw new NoSuchElementException();
        }
    }



    public String verifyToken(String token) {
        try {
            getPublicKey();
            log.info("Verifying token : " + token);
            HttpHeaders tokenHeaders = new HttpHeaders();
            log.info("Headers initialized");
            tokenHeaders.set("Authorization", "Bearer " + token);
            log.info("Token is set in headers: " + tokenHeaders.get("Authorization"));
            HttpEntity<Void> request = new HttpEntity<>(tokenHeaders);
            log.info("Request is created");
            ResponseEntity <String> response = accessDB.exchange("/auth/validate", HttpMethod.GET, request, String.class);
            log.info("Response is received : " + response.getBody());
            log.info("Token is valid");
            return response.getBody();
        } catch (NoSuchElementException e) {
            log.error("Token is not valid | " + e.getMessage());
            throw new NoSuchElementException();
        }
        catch (Exception e) {
            log.error("Token is not valid | " + e.getMessage());
            throw new NoSuchElementException();

        }
//        else {
//            log.error("Token is not valid");
//            throw new NoSuchElementException();
//            //System.out.println("Token is not valid");
//        }
    }

    public String extractUsernameFromToken(String token) throws Exception {
        log.info("Extracting username from token");
        HttpEntity<String> request = new HttpEntity<>(token, tokenService.getHeaders());
        ResponseEntity <String> response = accessDB.exchange("/token/username" , HttpMethod.POST, request, String.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            log.info("Token username is extracted");
            return response.getBody();
        } else {
            log.error("Token username is not extracted");
            throw new Exception("Token username is not extracted");
        }
    }




    public void addItemToCart(String username ,OrderItem orderItem) throws Exception {
        log.info("Adding item to cart");
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("username", username);
        requestBody.put("orderItem", orderItem);


        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, tokenService.getHeaders());
        ResponseEntity <String> response = accessDB.exchange("/cart/add" , HttpMethod.POST, request, String.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            log.info("Item added to cart");
        } else {
            log.error("Item not added to cart");
            throw new Exception("Item not added to cart");
        }
    }

    public void deleteItemFromCart(String username ,OrderItem orderItem) throws Exception {
        log.info("Deleting item from cart");
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("username", username);
        requestBody.put("orderItem", orderItem);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, tokenService.getHeaders());
        ResponseEntity <String> response = accessDB.exchange("/cart/delete" , HttpMethod.DELETE, request, String.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            log.info("Item deleted from cart");
        } else {
            log.error("Item not deleted from cart");
            throw new Exception("Item not deleted from cart");
        }
    }

    public List<OrderItem> getCart(String username) {
        log.info("Getting cart");
        HttpEntity<Void> request = new HttpEntity<>(tokenService.getHeaders());
        ResponseEntity <List<OrderItem>> responseUser  = accessDB.exchange("/cart/" + username , HttpMethod.GET, request, new ParameterizedTypeReference<List<OrderItem>>() {});
        if (responseUser.getStatusCode() == HttpStatus.OK) {
            return responseUser.getBody();
        } else {
            log.error("Cart is not found");
            throw new NoSuchElementException();
        }
    }

    public OrderItem addToCart(String username, Long itemId) {
        log.info("Adding item to cart");
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("username", username);
        requestBody.put("itemId", String.valueOf(itemId));
        HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, tokenService.getHeaders());
        ResponseEntity <OrderItem> response = accessDB.exchange("/cart/add" , HttpMethod.POST, request, OrderItem.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            log.error("Item not added to cart");
            throw new NoSuchElementException();
        }
    }
}
