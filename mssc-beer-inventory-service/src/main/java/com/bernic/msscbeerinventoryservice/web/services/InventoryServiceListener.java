package com.bernic.msscbeerinventoryservice.web.services;

import com.bernic.msscbeerinventoryservice.domain.BeerInventory;
import com.bernic.msscbeerinventoryservice.repositories.BeerInventoryRepository;
import com.bernic.mssccommonresources.config.JmsConfigConstants;
import com.bernic.mssccommonresources.events.NewInventoryEvent;
import com.bernic.mssccommonresources.web.model.BeerDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceListener {
    private final BeerInventoryRepository inventoryRepository;

    @JmsListener(destination = JmsConfigConstants.NEW_INVENTORY_QUEUE)
    public void brewSomeBeer(NewInventoryEvent inventoryEvent){
        log.debug("Got an inventory: " + inventoryEvent);
        BeerDto beerDto = inventoryEvent.getBeerDto();

        BeerInventory beerInventory = BeerInventory.builder()
                .beerId(beerDto.getId())
                .upc(beerDto.getUpc())
                .quantityOnHand(beerDto.getQuantityOnHand())
                .build();

        inventoryRepository.save(beerInventory);
    }
}
