package com.woody.shop.configuration;

import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ShopConfiguration
{
        @Bean
        public RestTemplate sslRestTemplate(RestTemplateBuilder builder, SslBundles sslBundles) {
            SslBundle sslBundle = sslBundles.getBundle("mybundle");
            RestTemplate restTemplate = builder.rootUri("https://localhost:8084").setSslBundle(sslBundle).build();
            restTemplate.setErrorHandler(new CustomResponseErrorHandler());
            return restTemplate;
        }

}
