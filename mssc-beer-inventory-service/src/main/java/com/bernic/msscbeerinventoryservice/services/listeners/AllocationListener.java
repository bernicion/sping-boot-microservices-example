package com.bernic.msscbeerinventoryservice.services.listeners;


import com.bernic.brewery.model.events.AllocateOrderRequest;
import com.bernic.brewery.model.events.AllocateOrderResult;
import com.bernic.msscbeerinventoryservice.config.JmsConfig;
import com.bernic.msscbeerinventoryservice.services.AllocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class AllocationListener {
    private final AllocationService allocationService;
    private final JmsTemplate jmsTemplate;

    @JmsListener(destination = JmsConfig.ALLOCATE_ORDER_QUEUE)
    public void listen(AllocateOrderRequest orderRequest) {
        AllocateOrderResult.AllocateOrderResultBuilder builder = AllocateOrderResult.builder();
        builder.beerOrderDto(orderRequest.getBeerOrderDto());
        try {
            Boolean allocationResult = allocationService.allocateOrder(orderRequest.getBeerOrderDto());
            builder.pendingInventory(!allocationResult);
            builder.allocationError(false);
        } catch (Exception e) {
            log.error("Allocation failed for Order ID: " + orderRequest.getBeerOrderDto().getId());
            builder.allocationError(true);
        }
        jmsTemplate.convertAndSend(JmsConfig.ALLOCATE_ORDER_RESPONSE_QUEUE, builder.build());
    }
}
