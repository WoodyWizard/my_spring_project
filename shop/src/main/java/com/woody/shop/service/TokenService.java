package com.woody.shop.service;

import com.woody.mydata.AuthRequest;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class TokenService {

    private String token = null;

    private HttpHeaders headers = new HttpHeaders();

    @Autowired
    @Qualifier("TokenDatabaseRest")
    private RestTemplate tokenDB;


    @Value("${gdatabase.username}")
    String username;
    @Value("${gdatabase.password}")
    String password;



    public HttpHeaders getHeaders() {
        return this.headers;
    }



    @PostConstruct
    public void checkTokenAfterInit() {
        try {
            log.info("Init Check of token");
            CheckTokenOrGenerate();
            log.info("New token is generated : " + this.token);
        } catch (Exception e) {
            log.error("Attempt to check token after init is failed");
            //throw new RuntimeException("Token not generated: \"checkTokenAfterInit\" ");
        }
    }

    public void CheckTokenOrGenerate() throws Exception {
        try {
            if (token == null || !TokenValidation()) {
                log.info("Request to attempt generate a token | Reason: [TOKEN IS NULL OR INVALID]");
                GenerateToken();
            }
        } catch (Exception e) {
            log.error("Attempt to generate token is failed");
            throw new Exception("Token are not generated");
        }
    }

    public Boolean TokenValidation() {
        try {
            log.info("Token Validation operation is started");
            headers.set("Authorization", "Bearer " + token);
            HttpEntity<Void> request = new HttpEntity<>(headers);
            ResponseEntity<String> response = tokenDB.exchange("/auth/validate", HttpMethod.GET, request, String.class);
            log.info("Token is valid");
            return true;
        } catch (HttpClientErrorException.NotFound e) {
            log.error("Token is not valid");
            return false;
        } catch (Exception e) {
            log.error("Token is not valid");
            return false;
        }
    }

    public Boolean GenerateToken() throws Exception {
        log.info("Generating token");
        if (username == null || password == null) {
            log.error("Username or password not set");
            throw new Exception("Username or password not set");
        }
        AuthRequest authRequest = new AuthRequest(username, password);
        ResponseEntity<String> response = tokenDB.postForEntity("/auth/token", authRequest, String.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            log.info("Token is generated");
            token = response.getBody();
            headers.set("Authorization", "Bearer " + token);
            log.info("Headers is changed to new token specification");
            return true;
        } else {
            token = null;
            log.error("Token not generated");
            throw new Exception("Token not generated");
        }
    }

}
