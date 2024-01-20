package com.woody.gdatabase.serializer;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.woody.mydata.Order;
import io.jsonwebtoken.io.SerializationException;
import io.jsonwebtoken.io.Serializer;

import java.io.OutputStream;

public class OrderSerializer implements Serializer<Order> {
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public byte[] serialize(Order order) throws SerializationException {
        try {
            return objectMapper.writeValueAsBytes(order);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new byte[0];
    }

    @Override
    public void serialize(Order order, OutputStream outputStream) throws SerializationException {

    }
}
