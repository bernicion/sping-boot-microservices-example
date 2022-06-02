package com.bernic.msscbeerservice.web.services.inventory;

import com.bernic.msscbeerservice.config.FailoverFeignClientConfig;
import com.bernic.msscbeerservice.web.services.inventory.model.BeerInventoryDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient(value = "inventory-failover", configuration = FailoverFeignClientConfig.class)
public interface InventoryFailoverFeignClient {

    @RequestMapping(method = RequestMethod.GET, value = "/inventory-failover")
    ResponseEntity<List<BeerInventoryDto>> getOnHandInventory();
}
