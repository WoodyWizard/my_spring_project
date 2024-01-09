package com.woody.shop.configuration;

import com.woody.mydata.token.TokenValidationException;
import com.woody.shop.service.ShopService;
import com.woody.shop.service.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

@Service
@Slf4j
public class CustomResponseErrorHandler implements ResponseErrorHandler {

    private final ResponseErrorHandler errorHandler = new DefaultResponseErrorHandler();

    @Autowired
    private TokenService tokenService;





    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return errorHandler.hasError(response);
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {

        try {
            log.info("Handling error");
            if (response.getStatusCode() == HttpStatus.FORBIDDEN) {
                log.info("Error is 403");
                log.info("Token generation");
                tokenService.CheckTokenOrGenerate();
            } else {
                log.error("Sending error to another ResponseErrorHandler [DISCONNECT FOR NOW]");
                //errorHandler.handleError(response);
            }
        } catch (Exception e) {
            log.error("Attempt to generate token is failed : ", e);
            throw new TokenValidationException("Token are not generated");
        }

    }

}
