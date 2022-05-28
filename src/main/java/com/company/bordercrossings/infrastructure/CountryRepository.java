package com.company.bordercrossings.infrastructure;

import com.company.bordercrossings.domain.Country;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;

public interface CountryRepository extends ReactiveSortingRepository<Country, String>, BorderCrossingRepository {
}