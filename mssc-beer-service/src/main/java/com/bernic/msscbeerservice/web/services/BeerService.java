package com.bernic.msscbeerservice.web.services;

import com.bernic.brewery.model.BeerDto;
import com.bernic.brewery.model.BeerPagedList;
import com.bernic.brewery.model.BeerStyleEnum;
import org.springframework.data.domain.PageRequest;

import java.util.UUID;

public interface BeerService {
    BeerPagedList listBeers(String beerName, BeerStyleEnum beerStyle, PageRequest pageRequest, Boolean showInventoryOnHand);

    BeerDto getBeerById(UUID uuid, Boolean showInventoryOnHand);

    BeerDto saveNewBeer(BeerDto beerDto);

    BeerDto updateBeer(UUID uuid, BeerDto beerDto);

    void deleteBeerById(UUID uuid);

    BeerDto getBeerByUpc(String beerUpc);
}
