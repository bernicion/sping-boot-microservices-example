package com.bernic.msscinventoryfailoverservice.web;

import com.bernic.brewery.model.BeerInventoryDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
public class InventoryFailoverController {
    @GetMapping("/inventory-failover")
    List<BeerInventoryDto> listBeersById() {
        log.debug("Failover Service Inventory");

        return List.of(BeerInventoryDto.builder()
                .id(UUID.randomUUID())
                .quantityOnHand(999)
                .beerId(UUID.fromString("00000000-0000-0000-0000-000000000000"))
                .createdDate(OffsetDateTime.now())
                .lastModifiedDate(OffsetDateTime.now())
                .build());
    }
}
