package com.bernic.msscbeerservice.bootstrap;

import com.bernic.msscbeerservice.repositories.BeerRepository;
import com.bernic.msscbeerservice.domain.Beer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;

@Component
public class BeerLoader implements CommandLineRunner {
    private final BeerRepository beerRepository;

    public BeerLoader(BeerRepository beerRepository) {
        this.beerRepository = beerRepository;
    }

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
                    .upc(323423423423L)
                    .price(new BigDecimal("6.5"))
                    .build();
            Beer beerTwo = Beer.builder()
                    .beerName("Galaxy Cat")
                    .beerStyle("PALE")
                    .quantityToBrew(150)
                    .minOnHand(10)
                    .upc(3231113423423L)
                    .price(new BigDecimal("10.5"))
                    .build();
            beerRepository.saveAll(Arrays.asList(beerOne, beerTwo));
        }
        System.out.println("Loadedd beers: " + beerRepository.count());
    }
}
