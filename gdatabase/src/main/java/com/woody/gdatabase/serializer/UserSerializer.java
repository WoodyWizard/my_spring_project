package com.woody.gdatabase.serializer;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.woody.mydata.User;
import io.jsonwebtoken.io.SerializationException;
import org.springframework.core.serializer.Serializer;


import java.io.IOException;
import java.io.OutputStream;

public class UserSerializer implements Serializer<User> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void serialize(User object, OutputStream outputStream) throws IOException {

    }

    @Override
    public byte[] serializeToByteArray(User object) throws IOException {
        return Serializer.super.serializeToByteArray(object);
    }
}
