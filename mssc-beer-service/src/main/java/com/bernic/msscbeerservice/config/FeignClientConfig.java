package com.bernic.msscbeerservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;

@Configuration
public class FeignClientConfig {

    @Bean
    public BasicAuthenticationInterceptor basicAuthenticationInterceptor(@Value("${sfg.brewery.inventory-user}") String inventoryUser,
                                                                         @Value("${sfg.brewery.inventory-password}") String inventoryPassword){
        return new BasicAuthenticationInterceptor(inventoryUser, inventoryPassword);
    }
}
