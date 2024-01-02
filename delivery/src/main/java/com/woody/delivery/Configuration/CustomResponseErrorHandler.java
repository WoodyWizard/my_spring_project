package com.woody.delivery.Configuration;

import com.woody.delivery.service.DeliveryService;
import com.woody.mydata.token.TokenValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

public class CustomResponseErrorHandler implements ResponseErrorHandler {

    private final ResponseErrorHandler errorHandler = new DefaultResponseErrorHandler();
    @Autowired
    private DeliveryService deliveryService;

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return errorHandler.hasError(response);
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {

        try {
            if (response.getStatusCode() == HttpStatus.FORBIDDEN) {
                deliveryService.CheckToken();
            }
        } catch (Exception e) {
            throw new TokenValidationException("Token not generated");
        }

        errorHandler.handleError(response);

    }
}
