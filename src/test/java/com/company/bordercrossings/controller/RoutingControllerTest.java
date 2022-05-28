package com.company.bordercrossings.controller;

import com.company.bordercrossings.domain.BorderCrossing;
import com.company.bordercrossings.infrastructure.CountryRepository;
import com.company.bordercrossings.infrastructure.exception.NoLandingCrossesException;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;

import static org.mockito.BDDMockito.given;

@WebFluxTest
public class RoutingControllerTest {

    private static final Logger logger = LoggerFactory.getLogger(RoutingControllerTest.class);

    @MockBean
    private CountryRepository countryRepository;

    @MockBean
    private ReactiveHashOperations<String, String, BorderCrossing> hashOperations;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void testGet_route_returnsOk(){

        BorderCrossing borderCrossing = new BorderCrossing(Arrays.asList("CZE", "AUT", "ITA"));

        given(countryRepository.queryBorderCrossing("CZE", "ITA")).willReturn(Mono.just(borderCrossing));

        webTestClient.get().uri("/routing/CZE/ITA")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                    .jsonPath("$.length()").isNumber()
                    .jsonPath("$.length()").isEqualTo("1")
                    .jsonPath("$.route.[0]").isEqualTo("CZE")
                    .jsonPath("$.route.[1]").isEqualTo("AUT")
                    .jsonPath("$.route.[2]").isEqualTo("ITA")
                    .consumeWith(response -> logger.info(response.toString()));

    }

    @Test
    public void testGet_route_returns400(){

        given(countryRepository.queryBorderCrossing("XXX", "YYY")).willReturn(Mono.error(new NoLandingCrossesException()));

        webTestClient.get().uri("/routing/XXX/YYY")
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody()
                .consumeWith(response -> logger.info(response.toString()));

    }

}
