package com.bernic.msscbeerinventoryservice.web.mappers;

import com.bernic.msscbeerinventoryservice.domain.BeerInventory;
import com.bernic.brewery.model.BeerInventoryDto;
import org.mapstruct.Mapper;

/**
 * Created by jt on 2019-05-31.
 */
@Mapper(componentModel = "spring", uses = {DateMapper.class})
public interface BeerInventoryMapper {

    BeerInventory beerInventoryDtoToBeerInventory(BeerInventoryDto beerInventoryDTO);

    BeerInventoryDto beerInventoryToBeerInventoryDto(BeerInventory beerInventory);
}
