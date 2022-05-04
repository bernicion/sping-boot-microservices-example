package com.bernic.msscbeerorderservice.services.beer;

import com.bernic.msscbeerorderservice.services.beer.model.BeerDto;

import java.util.Optional;
import java.util.UUID;

public interface BeerService {

    Optional<BeerDto> getBeerById(UUID uuid);

    Optional<BeerDto> getBeerByUpc(String upc);
}
