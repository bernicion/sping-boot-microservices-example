package com.bernic.msscbeerinventoryservice.services;

import com.bernic.brewery.model.events.BeerDto;
import com.bernic.brewery.model.events.NewInventoryEvent;
import com.bernic.msscbeerinventoryservice.config.JmsConfig;
import com.bernic.msscbeerinventoryservice.domain.BeerInventory;
import com.bernic.msscbeerinventoryservice.repositories.BeerInventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceListener {
    private final BeerInventoryRepository inventoryRepository;

    @JmsListener(destination = JmsConfig.NEW_INVENTORY_QUEUE)
    public void brewSomeBeer(NewInventoryEvent inventoryEvent){
        log.debug("An event tot brew some beer was received. Event: : " + inventoryEvent);
        BeerDto beerDto = inventoryEvent.getBeerDto();

        BeerInventory beerInventory = BeerInventory.builder()
                .beerId(beerDto.getId())
                .upc(beerDto.getUpc())
                .quantityOnHand(beerDto.getQuantityOnHand())
                .build();

        inventoryRepository.save(beerInventory);
    }
}
