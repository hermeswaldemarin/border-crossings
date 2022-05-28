package com.company.bordercrossings.infrastructure;

import com.company.bordercrossings.domain.BorderCrossing;
import com.company.bordercrossings.domain.Country;
import com.company.bordercrossings.infrastructure.exception.NoLandingCrossesException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.SetOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

public class BorderCrossingRepositoryImpl implements BorderCrossingRepository {

    private final ReactiveMongoTemplate mongoTemplate;

    @Autowired
    public BorderCrossingRepositoryImpl(ReactiveMongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Mono<BorderCrossing> queryBorderCrossing(String origin, String destination) {
        Aggregation aggregation = newAggregation(
                match(Criteria.where("cca3").in(Arrays.asList(origin, destination))),
                group()
                        .first("borders").as("set1")
                        .last("borders").as("set2"),
                project("set1", "set2").and(SetOperators.arrayAsSet("set1")
                                .intersects("set2")).as("borders"),
                project("borders")

        );

        return mongoTemplate.aggregate(aggregation, Country.class, Country.class)
                .map(ws -> {
                    List<String> completeRoute = Stream.concat(
                                    Arrays.asList(origin).stream(),
                                    Stream.concat(
                                            ws.borders().stream(),
                                            Arrays.asList(destination).stream()
                                    )
                            )
                            .collect(Collectors.toList());
                    return new BorderCrossing(completeRoute);
                }).next().switchIfEmpty(Mono.error(new NoLandingCrossesException()));
    }

}