package com.company.bordercrossings.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "country")
@Data
@AllArgsConstructor
@ToString
public class Country {
    private String cca3;
    private List<String> borders;
}
