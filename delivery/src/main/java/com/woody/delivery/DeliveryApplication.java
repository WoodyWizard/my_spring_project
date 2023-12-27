package com.woody.delivery;

import com.woody.mydata.token.TokenValidationException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@EntityScan("com.woody.mydata")
@SpringBootApplication
public class DeliveryApplication {


	public static void main(String[] args) {
		try {
			SpringApplication.run(DeliveryApplication.class, args);
		} catch (TokenValidationException e) {
			System.exit(1);
		}
	}

}
