package com.bernic.msscbeerinventoryservice.services.listeners;

import com.bernic.brewery.model.events.DeallocateOrderRequest;
import com.bernic.msscbeerinventoryservice.config.JmsConfig;
import com.bernic.msscbeerinventoryservice.services.AllocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class DeallocationListener {
    private final AllocationService allocationService;

    @JmsListener(destination = JmsConfig.DEALLOCATE_ORDER_QUEUE)
    public void listen(DeallocateOrderRequest orderRequest) {
        allocationService.deallocateOrder(orderRequest.getBeerOrderDto());
    }
}
