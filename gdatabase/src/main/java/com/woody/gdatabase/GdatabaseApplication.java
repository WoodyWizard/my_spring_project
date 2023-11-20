package com.woody.gdatabase;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@EntityScan("com.woody.mydata")
@SpringBootApplication
public class GdatabaseApplication {

	public static void main(String[] args) {
		SpringApplication.run(GdatabaseApplication.class, args);
	}

}
