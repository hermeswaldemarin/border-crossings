package com.company.bordercrossings.infrastructure;

import com.company.bordercrossings.domain.BorderCrossing;
import com.company.bordercrossings.domain.Country;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface BorderCrossingRepository {

    Mono<BorderCrossing> queryBorderCrossing(String origin, String destination);

}