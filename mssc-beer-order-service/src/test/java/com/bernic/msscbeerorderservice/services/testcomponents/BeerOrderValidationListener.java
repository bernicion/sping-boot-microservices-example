package com.bernic.msscbeerorderservice.services.testcomponents;

import com.bernic.brewery.model.events.ValidateOrderRequest;
import com.bernic.brewery.model.events.ValidateOrderResult;
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
public class BeerOrderValidationListener {
    private final JmsTemplate jmsTemplate;

    @JmsListener(destination = JmsConfig.VALIDATE_ORDER_QUEUE)
    public void list(Message message) {
        ValidateOrderRequest request = (ValidateOrderRequest) message.getPayload();

        log.debug("########### DUMMY Listener###############");
        jmsTemplate.convertAndSend(JmsConfig.VALIDATE_ORDER_RESPONSE_QUEUE,
                ValidateOrderResult.builder()
                        .isValid(true)
                        .orderId(request.getBeerOrderDto().getId())
                        .build());
    }
}
