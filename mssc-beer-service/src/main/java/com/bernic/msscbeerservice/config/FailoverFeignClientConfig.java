package com.bernic.msscbeerservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;

@Configuration
public class FailoverFeignClientConfig {
    @Bean
    public BasicAuthenticationInterceptor basicFailoverAuthenticationInterceptor(@Value("${sfg.brewery.inventoryfailover-user}") String inventoryUser,
                                                                         @Value("${sfg.brewery.inventoryfailover-password}") String inventoryPassword){
        return new BasicAuthenticationInterceptor(inventoryUser, inventoryPassword);
    }
}
