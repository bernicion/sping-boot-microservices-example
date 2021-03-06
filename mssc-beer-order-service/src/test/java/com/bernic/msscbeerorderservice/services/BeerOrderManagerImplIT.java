package com.bernic.msscbeerorderservice.services;

import com.bernic.brewery.model.BeerDto;
import com.bernic.brewery.model.events.AllocationFailureEvent;
import com.bernic.brewery.model.events.DeallocateOrderRequest;
import com.bernic.msscbeerorderservice.config.JmsConfig;
import com.bernic.msscbeerorderservice.domain.BeerOrder;
import com.bernic.msscbeerorderservice.domain.BeerOrderLine;
import com.bernic.msscbeerorderservice.domain.BeerOrderStatusEnum;
import com.bernic.msscbeerorderservice.domain.Customer;
import com.bernic.msscbeerorderservice.repositories.BeerOrderRepository;
import com.bernic.msscbeerorderservice.repositories.CustomerRepository;
import com.bernic.msscbeerorderservice.services.beer.BeerServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jenspiegsa.wiremockextension.WireMockExtension;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.core.JmsTemplate;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.jenspiegsa.wiremockextension.ManagedWireMockServer.with;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.awaitility.Awaitility.await;
import static org.awaitility.Awaitility.waitAtMost;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(WireMockExtension.class)
@SpringBootTest
public class BeerOrderManagerImplIT {
    private static final String BEER_UPC = "0631234200036";
    @Autowired
    BeerOrderManager beerOrderManager;
    @Autowired
    BeerOrderRepository beerOrderRepository;
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    WireMockServer wireMockServer;
    @Autowired
    JmsTemplate jmsTemplate;

    private Customer customer;
    private UUID beerId = UUID.randomUUID();

    @TestConfiguration
    static class RestTemplateBuilderProvider {
        @Bean(destroyMethod = "stop")
        public WireMockServer wireMockServer() {
            WireMockServer server = with(wireMockConfig().port(8083));
            server.start();
            return server;
        }
    }

    @BeforeEach
    void setUp() {
        customer = customerRepository.save(Customer.builder()
                .customerName("Test Customer")
                .build());
    }

    @Test
    public void testNewToAllocated() throws JsonProcessingException {
        BeerDto beerDto = BeerDto.builder().id(beerId).upc(BEER_UPC).build();

        wireMockServer.stubFor(get(BeerServiceImpl.BEER_UPC_PATH_V1 + BEER_UPC)
                .willReturn(okJson(objectMapper.writeValueAsString(beerDto))));

        BeerOrder beerOrder = createBeerOrder();

        BeerOrder savedBeerOrder = beerOrderManager.newBeerOrder(beerOrder);

        waitAtMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            BeerOrder foundOrder = beerOrderRepository.findById(beerOrder.getId()).get();
            assertEquals(BeerOrderStatusEnum.ALLOCATED, foundOrder.getOrderStatus());
        });

        waitAtMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            BeerOrder foundOrder = beerOrderRepository.findById(beerOrder.getId()).get();
            BeerOrderLine line = foundOrder.getBeerOrderLines().iterator().next();
            assertEquals(line.getOrderQuantity(), line.getQuantityAllocated());
        });

        BeerOrder savedBeerOrder2 = beerOrderRepository.findById(savedBeerOrder.getId()).get();

        assertNotNull(savedBeerOrder2);
        assertEquals(BeerOrderStatusEnum.ALLOCATED, savedBeerOrder2.getOrderStatus());
        savedBeerOrder2.getBeerOrderLines().forEach(line -> {
            assertEquals(line.getOrderQuantity(), line.getQuantityAllocated());
        });
    }

    @Test
    public void testFailedValidation() throws JsonProcessingException {
        BeerDto beerDto = BeerDto.builder().id(beerId).upc(BEER_UPC).build();

        wireMockServer.stubFor(get(BeerServiceImpl.BEER_UPC_PATH_V1 + BEER_UPC)
                .willReturn(okJson(objectMapper.writeValueAsString(beerDto))));

        BeerOrder beerOrder = createBeerOrder();
        beerOrder.setCustomerRef("fail-validation");

        beerOrderManager.newBeerOrder(beerOrder);
        waitAtMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            BeerOrder foundOrder = beerOrderRepository.findById(beerOrder.getId()).get();
            assertEquals(BeerOrderStatusEnum.VALIDATION_EXCEPTION, foundOrder.getOrderStatus());
        });
    }

    @Test
    public void testFailedAllocation() throws JsonProcessingException {
        BeerDto beerDto = BeerDto.builder().id(beerId).upc(BEER_UPC).build();

        wireMockServer.stubFor(get(BeerServiceImpl.BEER_UPC_PATH_V1 + BEER_UPC)
                .willReturn(okJson(objectMapper.writeValueAsString(beerDto))));

        BeerOrder beerOrder = createBeerOrder();
        beerOrder.setCustomerRef("fail-allocation");

        BeerOrder savedBeerOrder = beerOrderManager.newBeerOrder(beerOrder);
        waitAtMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            BeerOrder foundOrder = beerOrderRepository.findById(beerOrder.getId()).get();
            assertEquals(BeerOrderStatusEnum.ALLOCATION_EXCEPTION, foundOrder.getOrderStatus());
        });
        AllocationFailureEvent failureEvent = (AllocationFailureEvent) jmsTemplate.receiveAndConvert(JmsConfig.ALLOCATION_FAILURE_QUEUE);
        assertNotNull(failureEvent);
        assertEquals(failureEvent.getOrderId(), savedBeerOrder.getId());
    }

    @Test
    public void testPartialAllocation() throws JsonProcessingException {
        BeerDto beerDto = BeerDto.builder().id(beerId).upc(BEER_UPC).build();

        wireMockServer.stubFor(get(BeerServiceImpl.BEER_UPC_PATH_V1 + BEER_UPC)
                .willReturn(okJson(objectMapper.writeValueAsString(beerDto))));

        BeerOrder beerOrder = createBeerOrder();
        beerOrder.setCustomerRef("partial-allocation");

        beerOrderManager.newBeerOrder(beerOrder);
        waitAtMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            BeerOrder foundOrder = beerOrderRepository.findById(beerOrder.getId()).get();
            assertEquals(BeerOrderStatusEnum.PENDING_INVENTORY, foundOrder.getOrderStatus());
        });

    }

    @Test
    public void testNewToPickup() throws JsonProcessingException {
        BeerDto beerDto = BeerDto.builder().id(beerId).upc(BEER_UPC).build();

        wireMockServer.stubFor(get(BeerServiceImpl.BEER_UPC_PATH_V1 + BEER_UPC)
                .willReturn(okJson(objectMapper.writeValueAsString(beerDto))));

        BeerOrder beerOrder = createBeerOrder();

        BeerOrder savedBeerOrder = beerOrderManager.newBeerOrder(beerOrder);

        waitAtMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            BeerOrder foundOrder = beerOrderRepository.findById(beerOrder.getId()).get();
            assertEquals(BeerOrderStatusEnum.ALLOCATED, foundOrder.getOrderStatus());
        });

        beerOrderManager.beerOrderPickedUp(savedBeerOrder.getId());

        await().untilAsserted(() -> {
            BeerOrder foundOrder = beerOrderRepository.findById(beerOrder.getId()).get();
            assertEquals(BeerOrderStatusEnum.PICKED_UP, foundOrder.getOrderStatus());
        });

        BeerOrder pickedUpOrder = beerOrderRepository.findById(savedBeerOrder.getId()).get();

        assertEquals(BeerOrderStatusEnum.PICKED_UP, pickedUpOrder.getOrderStatus());
    }

    @Test
    public void testValidationPendingToCancel() throws JsonProcessingException {
        BeerDto beerDto = BeerDto.builder().id(beerId).upc(BEER_UPC).build();

        wireMockServer.stubFor(get(BeerServiceImpl.BEER_UPC_PATH_V1 + BEER_UPC)
                .willReturn(okJson(objectMapper.writeValueAsString(beerDto))));

        BeerOrder beerOrder = createBeerOrder();
        beerOrder.setCustomerRef("dont-validate");

        BeerOrder savedBeerOrder = beerOrderManager.newBeerOrder(beerOrder);

        waitAtMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            BeerOrder foundOrder = beerOrderRepository.findById(beerOrder.getId()).get();
            assertEquals(BeerOrderStatusEnum.VALIDATION_PENDING, foundOrder.getOrderStatus());
        });

        beerOrderManager.cancelOrder(savedBeerOrder.getId());

        waitAtMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            BeerOrder foundOrder = beerOrderRepository.findById(beerOrder.getId()).get();
            assertEquals(BeerOrderStatusEnum.CANCELED, foundOrder.getOrderStatus());
        });
    }

    @Test
    public void testAllocationPendingToCancel() throws JsonProcessingException {
        BeerDto beerDto = BeerDto.builder().id(beerId).upc(BEER_UPC).build();

        wireMockServer.stubFor(get(BeerServiceImpl.BEER_UPC_PATH_V1 + BEER_UPC)
                .willReturn(okJson(objectMapper.writeValueAsString(beerDto))));

        BeerOrder beerOrder = createBeerOrder();
        beerOrder.setCustomerRef("dont-allocate");

        BeerOrder savedBeerOrder = beerOrderManager.newBeerOrder(beerOrder);

        waitAtMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            BeerOrder foundOrder = beerOrderRepository.findById(beerOrder.getId()).get();
            assertEquals(BeerOrderStatusEnum.ALLOCATION_PENDING, foundOrder.getOrderStatus());
        });

        beerOrderManager.cancelOrder(savedBeerOrder.getId());

        waitAtMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            BeerOrder foundOrder = beerOrderRepository.findById(beerOrder.getId()).get();
            assertEquals(BeerOrderStatusEnum.CANCELED, foundOrder.getOrderStatus());
        });
    }

    @Test
    public void testAllocatedToCancel() throws JsonProcessingException {
        BeerDto beerDto = BeerDto.builder().id(beerId).upc(BEER_UPC).build();

        wireMockServer.stubFor(get(BeerServiceImpl.BEER_UPC_PATH_V1 + BEER_UPC)
                .willReturn(okJson(objectMapper.writeValueAsString(beerDto))));

        BeerOrder beerOrder = createBeerOrder();

        BeerOrder savedBeerOrder = beerOrderManager.newBeerOrder(beerOrder);

        waitAtMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            BeerOrder foundOrder = beerOrderRepository.findById(beerOrder.getId()).get();
            assertEquals(BeerOrderStatusEnum.ALLOCATED, foundOrder.getOrderStatus());
        });

        beerOrderManager.cancelOrder(savedBeerOrder.getId());

        waitAtMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            BeerOrder foundOrder = beerOrderRepository.findById(beerOrder.getId()).get();
            assertEquals(BeerOrderStatusEnum.CANCELED, foundOrder.getOrderStatus());
        });

        DeallocateOrderRequest failureEvent = (DeallocateOrderRequest) jmsTemplate.receiveAndConvert(JmsConfig.DEALLOCATE_ORDER_QUEUE);
        assertNotNull(failureEvent);
        assertEquals(failureEvent.getBeerOrderDto().getId(), savedBeerOrder.getId());
    }

    public BeerOrder createBeerOrder() {
        BeerOrder beerOrder = BeerOrder.builder()
                .customer(customer)
                .build();
        Set<BeerOrderLine> lines = new HashSet<>();
        lines.add(BeerOrderLine.builder()
                .beerId(beerId)
                .upc(BEER_UPC)
                .orderQuantity(1)
                .beerOrder(beerOrder)
                .build());
        beerOrder.setBeerOrderLines(lines);
        return beerOrder;
    }
}
