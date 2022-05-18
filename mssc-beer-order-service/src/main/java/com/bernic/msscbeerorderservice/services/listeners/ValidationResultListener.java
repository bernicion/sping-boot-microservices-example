package com.bernic.msscbeerorderservice.services.listeners;

import com.bernic.brewery.model.events.ValidateOrderResult;
import com.bernic.msscbeerorderservice.config.JmsConfig;
import com.bernic.msscbeerorderservice.services.BeerOrderManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class ValidationResultListener {

    private final BeerOrderManager beerOrderManager;

    @JmsListener(destination = JmsConfig.VALIDATE_ORDER_RESPONSE_QUEUE)
    public void listenForBeerOrderResult(ValidateOrderResult validateOrderResult) {
        final UUID beerOrderId = validateOrderResult.getOrderId();

        log.debug("Validation result for Order ID: " + beerOrderId);

        beerOrderManager.processValidationResult(beerOrderId, validateOrderResult.getIsValid());
    }
}
