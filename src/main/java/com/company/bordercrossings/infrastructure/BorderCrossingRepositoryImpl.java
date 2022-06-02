package com.company.bordercrossings.infrastructure;

import com.company.bordercrossings.domain.BorderCrossing;
import com.company.bordercrossings.domain.Country;
import com.company.bordercrossings.infrastructure.exception.NoLandingCrossesException;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.SetOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.redis.core.ReactiveHashOperations;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

public class BorderCrossingRepositoryImpl implements BorderCrossingRepository {

    private final ReactiveMongoTemplate mongoTemplate;
    private final ReactiveHashOperations<String, String, BorderCrossing> hashOperations;
    private static final String KEY = "routing-table";

    public BorderCrossingRepositoryImpl(ReactiveMongoTemplate mongoTemplate, ReactiveHashOperations<String, String, BorderCrossing> hashOperations) {
        this.mongoTemplate = mongoTemplate;
        this.hashOperations = hashOperations;
    }

    @Override
    public Mono<BorderCrossing> queryBorderCrossing(String origin, String destination) {
        BorderCrossing bc;
        try {
            bc = hashOperations.get(KEY, String.format("#%s#%s", origin, destination)).toFuture().get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        return bc!=null?Mono.just(bc):this.queryBorderCrossingFromDatabase(origin, destination);
    }

    private Mono<BorderCrossing> queryBorderCrossingFromDatabase(String origin, String destination) {
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
                                    Stream.of(origin),
                                    Stream.concat(
                                            ws.getBorders().stream(),
                                            Stream.of(destination)
                                    )
                            )
                            .collect(Collectors.toList());

                    return new BorderCrossing(completeRoute);
                })
                .flatMap(dto -> this.hashOperations.put(KEY, String.format("#%s#%s", origin, destination), dto).thenReturn(dto))
                .next()
                .switchIfEmpty(Mono.error(new NoLandingCrossesException()));
    }

}