package com.bernic.msscbeerorderservice.services.testcomponents;

import com.bernic.brewery.model.events.AllocateOrderRequest;
import com.bernic.brewery.model.events.AllocateOrderResult;
import com.bernic.msscbeerorderservice.config.JmsConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class BeerOrderAllocationListener {

    private final JmsTemplate jmsTemplate;

    @JmsListener(destination = JmsConfig.ALLOCATE_ORDER_QUEUE)
    public void listen(Message msg) {
        boolean isAllocationFailure = false;
        boolean isPendingInventory = false;
        boolean sendResponse = true;

        AllocateOrderRequest request = (AllocateOrderRequest) msg.getPayload();

        String customerRef = request.getBeerOrderDto().getCustomerRef();
        if ("partial-allocation".equals(customerRef)) {
            isPendingInventory = true;
        }
        if ("fail-allocation".equals(customerRef)) {
            isAllocationFailure = true;
        }
        if ("dont-allocate".equals(customerRef)) {
            sendResponse = false;
        }

        final boolean finalPendingInventory = isPendingInventory;
        request.getBeerOrderDto().getBeerOrderLines().forEach(beerOrderLineDto -> {
            if (finalPendingInventory) {
                beerOrderLineDto.setQuantityAllocated(beerOrderLineDto.getOrderQuantity() - 1);
            } else {
                beerOrderLineDto.setQuantityAllocated(beerOrderLineDto.getOrderQuantity());
            }
        });
        if(sendResponse) {
            jmsTemplate.convertAndSend(JmsConfig.ALLOCATE_ORDER_RESPONSE_QUEUE,
                    AllocateOrderResult.builder()
                            .beerOrderDto(request.getBeerOrderDto())
                            .pendingInventory(isPendingInventory)
                            .allocationError(isAllocationFailure)
                            .build());
        }
    }
}
