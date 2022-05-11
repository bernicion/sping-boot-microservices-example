package com.bernic.msscbeerservice.web.services.brewing;

import com.bernic.msscbeerservice.domain.Beer;
import com.bernic.msscbeerservice.repositories.BeerRepository;
import com.bernic.mssccommonresources.config.JmsConfigConstants;
import com.bernic.mssccommonresources.events.BrewBeerEvent;
import com.bernic.mssccommonresources.events.NewInventoryEvent;
import com.bernic.mssccommonresources.web.model.BeerDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BrewBeerListener {

    private final BeerRepository beerRepository;

    private final JmsTemplate jmsTemplate;

    @Transactional
    @JmsListener(destination = JmsConfigConstants.BREWING_REQUEST_QUEUE)
    public void listen(BrewBeerEvent brewBeerEvent) {
        BeerDto beerDto = brewBeerEvent.getBeerDto();

        Beer beer = beerRepository.getById(beerDto.getId());
        beerDto.setQuantityOnHand(beer.getQuantityToBrew());//simplest implementation

        NewInventoryEvent newInventoryEvent = new NewInventoryEvent(beerDto);
        log.debug(String.format("Brewed beer with UPC %s, Min on Hand: %s, QOH: %s",
                beerDto.getUpc(), beer.getMinOnHand(), beerDto.getQuantityOnHand()));

        jmsTemplate.convertAndSend(JmsConfigConstants.NEW_INVENTORY_QUEUE, newInventoryEvent);
    }
}
