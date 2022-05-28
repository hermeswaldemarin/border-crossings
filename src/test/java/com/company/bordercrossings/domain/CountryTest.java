package com.company.bordercrossings.domain;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class CountryTest {

    private static final Logger logger = LoggerFactory.getLogger(CountryTest.class);

    public Country createCountry() {
        return new Country("GRU", Arrays.asList("ARG", "PER"));
    }

    @Test
    public void equalsTest() {
        Country c1 = createCountry();
        Country c2 = createCountry();

        assertTrue(c1.equals(c2));
        assertEquals(c1, c2);
        assertEquals(c1.hashCode(), c2.hashCode());
    }

    @Test
    public void toStringTest() {
        Country c1 = createCountry();
        logger.info(c1.toString());

        assertEquals("Country[cca3=GRU, borders=[ARG, PER]]",
                c1.toString());
    }

    @Test
    public void accessorTest() {
        Country c1 = createCountry();
        assertEquals("GRU", c1.cca3());
    }

}