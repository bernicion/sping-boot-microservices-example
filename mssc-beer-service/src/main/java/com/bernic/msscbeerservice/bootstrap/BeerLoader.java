package com.bernic.msscbeerservice.bootstrap;

import com.bernic.msscbeerservice.repositories.BeerRepository;
import com.bernic.msscbeerservice.domain.Beer;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;

@RequiredArgsConstructor
@Component
public class BeerLoader implements CommandLineRunner {
    private static final String BEER_UPC_1 = "0631234200036";
    private static final String BEER_UPC_2 = "0631234300019";
    private static final String BEER_UPC_3 = "0083783375213";

    private final BeerRepository beerRepository;

    @Override
    public void run(String... args) throws Exception {
            loadBeerObjects();
    }

    private void loadBeerObjects() {
        if(beerRepository.count() == 0){
            Beer beerOne = Beer.builder()
                    .beerName("Mango Bobs")
                    .beerStyle("IPA")
                    .quantityToBrew(200)
                    .minOnHand(12)
                    .upc(BEER_UPC_1)
                    .price(new BigDecimal("6.5"))
                    .build();
            Beer beerTwo = Beer.builder()
                    .beerName("Galaxy Cat")
                    .beerStyle("PORTER")
                    .quantityToBrew(150)
                    .minOnHand(10)
                    .upc(BEER_UPC_2)
                    .price(new BigDecimal("10.5"))
                    .build();

            Beer beerThree = Beer.builder()
                    .beerName("No hammers on the bar")
                    .beerStyle("LAGER")
                    .quantityToBrew(100)
                    .minOnHand(14)
                    .upc(BEER_UPC_3)
                    .price(new BigDecimal("7.5"))
                    .build();
            beerRepository.saveAll(Arrays.asList(beerOne, beerTwo, beerThree));
        }
        System.out.println("Loaded beers: " + beerRepository.count());
    }
}
