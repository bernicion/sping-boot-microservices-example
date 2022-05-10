package com.bernic.msscbeerservice.web.services.brewing;

import com.bernic.msscbeerservice.config.JmsConfig;
import com.bernic.msscbeerservice.domain.Beer;
import com.bernic.msscbeerservice.events.BrewBeerEvent;
import com.bernic.msscbeerservice.repositories.BeerRepository;
import com.bernic.msscbeerservice.web.mappers.BeerMapper;
import com.bernic.msscbeerservice.web.services.inventory.BeerInventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BrewingService {
    private final BeerRepository beerRepository;
    private final BeerInventoryService beerInventoryService;
    private final JmsTemplate jmsTemplate;
    private final BeerMapper beerMapper;

    @Scheduled(fixedRate = 5000) //every 5 seconds
    public void checkForLowInventory() {
        List<Beer> beerList = beerRepository.findAll();
        beerList.forEach(beer -> {
            Integer inventoryQOH = beerInventoryService.getOnhandInventory(beer.getId());
            log.debug(String.format("Min on Hand for Beer With UPC %s is %s, real inventory is %s: ",
                    beer.getUpc(), beer.getMinOnHand(), inventoryQOH));
            if (beer.getMinOnHand() >= inventoryQOH) {
                jmsTemplate.convertAndSend(JmsConfig.BREWING_REQUEST_QUEUE, new BrewBeerEvent(beerMapper.beerToBeerDto(beer)));
            }
        });
    }
}
