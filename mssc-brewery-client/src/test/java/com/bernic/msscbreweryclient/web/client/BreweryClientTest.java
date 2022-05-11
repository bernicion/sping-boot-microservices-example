package com.bernic.msscbreweryclient.web.client;

import com.bernic.mssccommonresources.web.model.BeerDto;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.URI;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Disabled("This is integration test, be sure beer service is started")
class BreweryClientTest {

    @Autowired
    BreweryClient breweryClient;

    @Test
    public void getBeerById() {
        BeerDto beerDto = breweryClient.getBeerById(UUID.randomUUID());
        assertNotNull(beerDto);
    }

    @Test
    public void testSaveNewBeer() {
        BeerDto beerDto = BeerDto.builder().beerName("Some beer name").build();
        URI uri = breweryClient.saveNewBeer(beerDto);
        assertNotNull(uri);
    }


    @Test
    public void testUpdateBeer() {
        BeerDto beerDto = BeerDto.builder().beerName("Some beer name").build();
        breweryClient.updateExistingBeer(UUID.randomUUID(), beerDto);
    }

    @Test
    public void testDeleteBeer() {
        breweryClient.deleteBeer(UUID.randomUUID());
    }
}
