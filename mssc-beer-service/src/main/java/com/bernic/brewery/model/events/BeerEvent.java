package com.bernic.brewery.model.events;

import com.bernic.brewery.model.BeerDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BeerEvent implements Serializable {
    @Serial
    private static final long serialVersionUID = -9070624781283567628L;

    private BeerDto beerDto;

}
