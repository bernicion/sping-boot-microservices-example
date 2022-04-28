package com.bernic.msscbeerservice.web.controller;

import com.bernic.msscbeerservice.repositories.BeerRepository;
import com.bernic.msscbeerservice.web.model.BeerDto;
import com.bernic.msscbeerservice.web.model.BeerStyleEnum;
import com.bernic.msscbeerservice.web.services.BeerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.util.UUID;

@WebMvcTest({BeerController.class})
@ComponentScan(basePackages = {"com.bernic.msscbeerservice.web.mappers"})
class BeerControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    BeerService beerService;

    @MockBean
    BeerRepository beerRepository;

    BeerDto beerDto;

    @BeforeEach
    public void setUp() {
        beerDto = BeerDto.builder()
                .beerName("Some Random Name")
                .beerStyle(BeerStyleEnum.IPA)
                .upc(100L)
                .price(BigDecimal.TEN)
                .build();
    }

    @Test
    void getBeerById() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/beer/" + UUID.randomUUID()).accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void saveNewBeer() throws Exception {
        String beerDtoToJson = objectMapper.writeValueAsString(beerDto);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/beer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(beerDtoToJson))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    void updateBeerById() throws Exception {
        String beerDtoToJson = objectMapper.writeValueAsString(beerDto);
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/beer/" + UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(beerDtoToJson))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }
}
