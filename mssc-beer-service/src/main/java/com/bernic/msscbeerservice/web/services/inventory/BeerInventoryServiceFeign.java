package com.bernic.msscbeerservice.web.services.inventory;

import com.bernic.msscbeerservice.web.services.inventory.model.BeerInventoryDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Profile("local-discovery")
@RequiredArgsConstructor
@Service
public class BeerInventoryServiceFeign implements BeerInventoryService {
    private final InventoryServiceFeignClient inventoryServiceFeignClient;

    @Override
    public Integer getOnhandInventory(UUID beerId) {

        ResponseEntity<List<BeerInventoryDto>> responseEntity = inventoryServiceFeignClient.getOnHandInventory(beerId);
        Integer onHand = Objects.requireNonNull(responseEntity.getBody())
                .stream()
                .mapToInt(BeerInventoryDto::getQuantityOnHand)
                .sum();
        log.debug("Call from Feign client -> Beer Id " + beerId + " On Hand is: " + onHand);
        return onHand;
    }
}
