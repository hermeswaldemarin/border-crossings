package com.company.bordercrossings.domain;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "country")
public record Country(String cca3, List<String> borders) {
}
