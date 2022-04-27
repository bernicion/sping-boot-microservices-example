package com.bernic.msscbeerservice.web.mappers;

import com.bernic.msscbeerservice.domain.Beer;
import com.bernic.msscbeerservice.web.model.BeerDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = DateMapper.class)
public interface BeerMapper {

    BeerDto beerToBeerDto(Beer beer);

    Beer beerDtoToBeer(BeerDto beerDto);
}
