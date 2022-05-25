package com.bernic.msscbeerorderservice.web.mappers;

import com.bernic.brewery.model.CustomerDto;
import com.bernic.msscbeerorderservice.domain.Customer;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {DateMapper.class})
public interface CustomerMapper {
    CustomerDto customerToDto(Customer customer);

    Customer dtoToCustomer(Customer dto);
}
