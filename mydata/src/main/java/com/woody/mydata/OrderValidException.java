package com.woody.mydata;

public class OrderValidException extends RuntimeException{

    public OrderValidException(){

    }

    public OrderValidException(String message){
        super(message);
    }


    public OrderValidException(String message, Throwable cause){
        super(message, cause);
    }
}
