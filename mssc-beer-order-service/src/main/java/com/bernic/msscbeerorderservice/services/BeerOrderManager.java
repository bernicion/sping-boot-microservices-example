package com.bernic.msscbeerorderservice.services;

import com.bernic.msscbeerorderservice.domain.BeerOrder;

public interface BeerOrderManager {
    BeerOrder newBeerOrder(BeerOrder beerOrder);
}
