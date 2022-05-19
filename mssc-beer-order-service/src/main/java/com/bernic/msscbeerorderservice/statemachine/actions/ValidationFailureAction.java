package com.bernic.msscbeerorderservice.statemachine.actions;

import com.bernic.msscbeerorderservice.domain.BeerOrderEventEnum;
import com.bernic.msscbeerorderservice.domain.BeerOrderStatusEnum;
import com.bernic.msscbeerorderservice.services.BeerOrderManagerImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ValidationFailureAction implements Action<BeerOrderStatusEnum, BeerOrderEventEnum> {

    @Override
    public void execute(StateContext<BeerOrderStatusEnum, BeerOrderEventEnum> stateContext) {
        String beerOrderId = (String) stateContext.getMessage().getHeaders().get(BeerOrderManagerImpl.ORDER_ID_HEADER);
        log.error("Compensating Transaction .... Validation Failed: " + beerOrderId);
    }
}
