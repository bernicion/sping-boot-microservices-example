package com.bernic.msscbeerservice.web.services;

import com.bernic.msscbeerservice.domain.Beer;
import com.bernic.msscbeerservice.repositories.BeerRepository;
import com.bernic.msscbeerservice.web.mappers.BeerMapper;
import com.bernic.msscbeerservice.web.model.BeerDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class BeerServiceImpl implements BeerService {

    private final BeerMapper beerMapper;
    private final BeerRepository beerRepository;

    @Override
    public BeerDto getBeerById(UUID uuid) {
        Beer beer = beerRepository.findById(uuid).get();
        return beerMapper.beerToBeerDto(beer);
    }

    @Override
    public BeerDto saveNewBeer(BeerDto beerDto) {
        Beer beer = beerMapper.beerDtoToBeer(beerDto);
        return beerMapper.beerToBeerDto(beerRepository.save(beer));
    }

    @Override
    public void updateBeer(UUID uuid, BeerDto beerDto) {
        beerRepository.findById(uuid).ifPresent(beer -> {
            beerRepository.save(beerMapper.beerDtoToBeer(beerDto));
        });
    }

    @Override
    public void deleteBeerById(UUID uuid) {
        beerRepository.deleteById(uuid);
    }
}
