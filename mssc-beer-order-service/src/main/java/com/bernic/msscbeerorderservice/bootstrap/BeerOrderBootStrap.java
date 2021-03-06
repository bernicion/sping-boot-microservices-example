package com.bernic.msscbeerorderservice.bootstrap;

import com.bernic.msscbeerorderservice.domain.Customer;
import com.bernic.msscbeerorderservice.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.UUID;

@RequiredArgsConstructor
@Component
@Slf4j
public class BeerOrderBootStrap implements CommandLineRunner {
    public static final String TASTING_ROOM = "Tasting Room";
    public static final String BEER_1_UPC = "0631234200036";
    public static final String BEER_2_UPC = "0631234300019";
    public static final String BEER_3_UPC = "0083783375213";

    private final CustomerRepository customerRepository;

    private void loadCustomerData() {
        if (customerRepository.findAllByCustomerName(BeerOrderBootStrap.TASTING_ROOM).size() == 0) {
            log.info("No Customer are present, so should add some...");
            Customer savedCustomer = customerRepository.save(Customer.builder()
                    .customerName(TASTING_ROOM)
                    .apiKey(UUID.randomUUID())
                    .build());
            log.info("Tasting Room Customer Id: " + savedCustomer.getId().toString());
        }
    }

    @Override
    public void run(String... args) throws Exception {
        loadCustomerData();
    }
}
