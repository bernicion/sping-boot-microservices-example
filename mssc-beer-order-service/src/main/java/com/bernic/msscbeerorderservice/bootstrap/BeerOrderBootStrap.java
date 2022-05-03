package com.bernic.msscbeerorderservice.bootstrap;

import com.bernic.msscbeerorderservice.domain.Customer;
import com.bernic.msscbeerorderservice.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@RequiredArgsConstructor
@Component
@Slf4j
public class BeerOrderBootStrap  {
    public static final String TASTING_ROOM = "Tasting Room";
    public static final String BEER_1_UPC = "0631234200036";
    public static final String BEER_2_UPC = "0631234300019";
    public static final String BEER_3_UPC = "0083783375213";

    private final CustomerRepository customerRepository;

    private void loadCustomerData() {
        if (customerRepository.count() == 0) {
            log.info("No Customer are present, so should add some...");
            customerRepository.save(Customer.builder()
                    .customerName(TASTING_ROOM)
                    .apiKey(UUID.randomUUID())
                    .build());
        }
        log.info("A total of " + customerRepository.count() + " customers are loaded");
    }

    @EventListener(ApplicationReadyEvent.class)
    public void run() {
        log.info("Trying to add some dummy data...");
        loadCustomerData();
    }
}
