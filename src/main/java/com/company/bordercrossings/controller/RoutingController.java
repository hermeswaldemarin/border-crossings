package com.company.bordercrossings.controller;

import com.company.bordercrossings.domain.BorderCrossing;
import com.company.bordercrossings.infrastructure.CountryRepository;
import com.company.bordercrossings.infrastructure.exception.NoLandingCrossesException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
public class RoutingController {
    private CountryRepository countryRepository;

    public RoutingController(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    @GetMapping("/routing/{origin}/{destination}")
    public Mono<BorderCrossing> getRoute(@PathVariable String origin, @PathVariable String destination){
        return countryRepository.queryBorderCrossing(origin, destination);
    }


    @ResponseStatus(
            value = HttpStatus.BAD_REQUEST,
            reason = "There is no landing crosses")
    @ExceptionHandler(NoLandingCrossesException.class)
    public void illegalArgumentHandler() {}

}
