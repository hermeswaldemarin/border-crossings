package com.company.bordercrossings.infrastructure;

import com.company.bordercrossings.domain.BorderCrossing;
import reactor.core.publisher.Mono;

public interface BorderCrossingRepository {

    Mono<BorderCrossing> queryBorderCrossing(String origin, String destination);

}