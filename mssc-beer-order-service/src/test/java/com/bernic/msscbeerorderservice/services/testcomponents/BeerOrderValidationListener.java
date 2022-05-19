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
        boolean isValid = true;
        boolean sendResponse = true;

        ValidateOrderRequest request = (ValidateOrderRequest) message.getPayload();
        //condition to fail validation
        String customerRef = request.getBeerOrderDto().getCustomerRef();
        if ("fail-validation".equals(customerRef)) {
            isValid = false;
        } else if("dont-validate".equals(customerRef)){
            sendResponse = false;
        }
        if(sendResponse) {
            jmsTemplate.convertAndSend(JmsConfig.VALIDATE_ORDER_RESPONSE_QUEUE,
                    ValidateOrderResult.builder()
                            .isValid(isValid)
                            .orderId(request.getBeerOrderDto().getId())
                            .build());
        }
    }
}
