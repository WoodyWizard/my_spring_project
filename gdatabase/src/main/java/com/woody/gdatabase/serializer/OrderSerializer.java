package com.woody.gdatabase.serializer;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.woody.mydata.Order;
import org.springframework.core.serializer.Serializer;


import java.io.IOException;
import java.io.OutputStream;

public class OrderSerializer implements Serializer<Order> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void serialize(Order object, OutputStream outputStream) throws IOException {

    }

    @Override
    public byte[] serializeToByteArray(Order object) throws IOException {
        return objectMapper.writeValueAsBytes(object);
    }
}
