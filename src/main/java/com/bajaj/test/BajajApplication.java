package com.bajaj.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class BajajApplication {

    public static void main(String[] args) {
        SpringApplication.run(BajajApplication.class, args);
    }

    // This allows us to inject RestTemplate to make API calls
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}