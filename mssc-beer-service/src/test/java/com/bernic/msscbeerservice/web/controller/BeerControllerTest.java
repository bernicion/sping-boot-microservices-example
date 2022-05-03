package com.bernic.msscbeerservice.web.controller;

import com.bernic.msscbeerservice.repositories.BeerRepository;
import com.bernic.msscbeerservice.web.model.BeerDto;
import com.bernic.msscbeerservice.web.model.BeerStyleEnum;
import com.bernic.msscbeerservice.web.services.BeerService;
import com.bernic.msscbeerservice.web.services.inventory.BeerInventoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.constraints.ConstraintDescriptions;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.restdocs.request.RequestDocumentation;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.snippet.Attributes.key;

@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "dev.someHostName", uriPort = 80)
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

    @MockBean
    BeerInventoryService beerInventoryService;

    @Test
    void getBeerById() throws Exception {
        given(beerService.getBeerById(any())).willReturn(mockABeer());
        ConstrainedFields fields = new ConstrainedFields(BeerDto.class);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/beer/{beerId}", UUID.randomUUID())
                        .param("isCold", "yes")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(
                        MockMvcRestDocumentation.document("v1/beer-get",
                                RequestDocumentation.pathParameters(
                                        parameterWithName("beerId").description("UUID of desired beer to get.")
                                ),
                                RequestDocumentation.requestParameters(
                                        parameterWithName("isCold").description("Is beer Cold Query Param")
                                ),
                                PayloadDocumentation.responseFields(
                                        fields.withPath("id").description("Id of Beer"),
                                        fields.withPath("version").description("Version number"),
                                        fields.withPath("createdDate").description("Date Created"),
                                        fields.withPath("lastModifiedDate").description("Date Updated"),
                                        fields.withPath("beerName").description("Beer Name"),
                                        fields.withPath("beerStyle").description("Beer Style"),
                                        fields.withPath("upc").description("UPC of Beer"),
                                        fields.withPath("price").description("Price"),
                                        fields.withPath("quantityOnHand").description("Quantity On hand")
                                )
                        )
                );
    }

    @Test
    void saveNewBeer() throws Exception {
        given(beerService.saveNewBeer(any())).willReturn(mockABeer());
        String beerDtoToJson = objectMapper.writeValueAsString(mockABeer());
        ConstrainedFields fields = new ConstrainedFields(BeerDto.class);

        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/beer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(beerDtoToJson))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andDo(
                        MockMvcRestDocumentation.document("v1/beer-new",
                                PayloadDocumentation.requestFields(
                                        fields.withPath("id").ignored(),
                                        fields.withPath("version").ignored(),
                                        fields.withPath("createdDate").ignored(),
                                        fields.withPath("lastModifiedDate").ignored(),
                                        fields.withPath("beerName").description("Name of the beer"),
                                        fields.withPath("beerStyle").description("Style of Beer"),
                                        fields.withPath("upc").description("Beer UPC").attributes(),
                                        fields.withPath("price").description("Beer Price"),
                                        fields.withPath("quantityOnHand").ignored()
                                )
                        )
                );
        ;
    }

    @Test
    void updateBeerById() throws Exception {
        given(beerService.updateBeer(any(), any())).willReturn(mockABeer());
        String beerDtoToJson = objectMapper.writeValueAsString(mockABeer());
        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/v1/beer/" + UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(beerDtoToJson))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    private BeerDto mockABeer() {
        return BeerDto.builder()
                .beerName("Some Random Name")
                .beerStyle(BeerStyleEnum.IPA)
                .upc("0631234200036")
                .price(BigDecimal.TEN)
                .build();
    }

    private static class ConstrainedFields {

        private final ConstraintDescriptions constraintDescriptions;

        ConstrainedFields(Class<?> input) {
            this.constraintDescriptions = new ConstraintDescriptions(input);
        }

        private FieldDescriptor withPath(String path) {
            return fieldWithPath(path).attributes(key("constraints").value(StringUtils
                    .collectionToDelimitedString(this.constraintDescriptions
                            .descriptionsForProperty(path), ". ")));
        }
    }
}
