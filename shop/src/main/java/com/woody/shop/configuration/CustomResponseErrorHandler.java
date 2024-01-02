package com.woody.shop.configuration;

import com.woody.mydata.token.TokenValidationException;
import com.woody.shop.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

public class CustomResponseErrorHandler implements ResponseErrorHandler {

    private final ResponseErrorHandler errorHandler = new DefaultResponseErrorHandler();
    @Autowired
    private ShopService shopService;

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return errorHandler.hasError(response);
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {

        try {
            if (response.getStatusCode() == HttpStatus.FORBIDDEN) {
                shopService.CheckToken();
            }
        } catch (Exception e) {
            throw new TokenValidationException("Token not generated");
        }

        errorHandler.handleError(response);

    }

}
