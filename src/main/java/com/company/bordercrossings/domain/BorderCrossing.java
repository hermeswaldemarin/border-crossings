package com.company.bordercrossings.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@AllArgsConstructor
@ToString
public class BorderCrossing {
    List<String> route;
}
