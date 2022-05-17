package com.bernic.msscbeerservice.web.services.order;

import com.bernic.brewery.model.BeerOrderDto;
import com.bernic.brewery.model.events.ValidateOrderRequest;
import com.bernic.brewery.model.events.ValidateOrderResult;
import com.bernic.msscbeerservice.config.JmsConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class BeerOrderValidationListener {

    private final BeerOrderValidation beerOrderValidation;
    private final JmsTemplate jmsTemplate;

    @JmsListener(destination = JmsConfig.VALIDATE_ORDER_QUEUE)
    public void validateOrderRequest(ValidateOrderRequest validateOrderRequest) {
        BeerOrderDto beerOrderDto = validateOrderRequest.getBeerOrderDto();
        Boolean isValid = beerOrderValidation.validateOrder(beerOrderDto);

        jmsTemplate.convertAndSend(JmsConfig.VALIDATE_ORDER_RESPONSE_QUEUE, ValidateOrderResult.builder()
                .orderId(beerOrderDto.getId())
                .isValid(isValid)
                .build()
        );
        log.debug("Sending back Validate Order result for Order ID:  " + beerOrderDto.getId());
    }

}
