package com.bernic.msscbeerservice.web.services;

import com.bernic.msscbeerservice.web.model.BeerDto;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class BeerServiceImpl implements BeerService{
    @Override
    public BeerDto getBeerById(UUID uuid) {
        //todo put the real impl
        return BeerDto.builder().build();
    }

    @Override
    public BeerDto saveNewBeer(BeerDto beerDto) {
        //todo put the real impl
        return BeerDto.builder().id(UUID.randomUUID()).build();
    }

    @Override
    public void updateBeer(UUID uuid, BeerDto beerDto) {
        //todo put the real impl
    }

    @Override
    public void deleteBeerById(UUID uuid) {
        //todo put the real impl
    }
}
