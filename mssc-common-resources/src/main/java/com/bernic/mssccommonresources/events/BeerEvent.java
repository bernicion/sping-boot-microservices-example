package com.bernic.mssccommonresources.events;

import com.bernic.mssccommonresources.web.model.BeerDto;
import lombok.*;

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
