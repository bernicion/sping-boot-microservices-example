package com.bernic.msscbeerservice.web.services;

import com.bernic.msscbeerservice.web.model.BeerDto;
import java.util.UUID;

public interface BeerService {

    BeerDto getBeerById(UUID uuid);

    BeerDto saveNewBeer(BeerDto beerDto);

    BeerDto updateBeer(UUID uuid, BeerDto beerDto);

    void deleteBeerById(UUID uuid);
}
