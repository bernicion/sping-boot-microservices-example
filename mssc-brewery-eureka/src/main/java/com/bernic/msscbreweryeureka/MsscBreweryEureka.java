package com.bernic.msscbreweryeureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class MsscBreweryEureka {
    public static void main(String[] args) {
        SpringApplication.run(MsscBreweryEureka.class);
    }
}
