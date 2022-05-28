package com.company.bordercrossings.repository;

import com.company.bordercrossings.domain.BorderCrossing;
import com.company.bordercrossings.domain.Country;
import com.company.bordercrossings.infrastructure.CountryRepository;
import com.company.bordercrossings.infrastructure.exception.NoLandingCrossesException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest(excludeAutoConfiguration = EmbeddedMongoAutoConfiguration.class)
public class CountryRepositoryTest {

    private static final Logger logger = LoggerFactory.getLogger(CountryRepositoryTest.class);

    @Autowired
    private CountryRepository countryRepository;

    private static final MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:bionic"))
            .withExposedPorts(27018)
            .withCopyFileToContainer(MountableFile.forClasspathResource("countries.json"),
                    "/countries.json")
            .withEnv("MONGO_INIT_DATABASE", "test");

    @BeforeAll
    public static void setUp() throws IOException, InterruptedException {
        mongoDBContainer.setPortBindings(List.of("27018:27018"));
        mongoDBContainer.start();

        Container.ExecResult result = mongoDBContainer.execInContainer("mongoimport",
                "--verbose", "--db=test", "--collection=country", "--file=/coutries.json", "--jsonArray");
        logger.info(result.getStdout());
        logger.info(result.getStderr());
        logger.info("exit code={}", result.getExitCode());
    }

    @Test
    public void shouldQueryBorderCrossingReturnItens(){
        Mono<BorderCrossing> result = countryRepository.queryBorderCrossing("CZE", "ITA");
        BorderCrossing borderCrossing = result.block();

        assertThat(result).isNotNull();
        assertThat(borderCrossing.route().get(0)).isEqualTo("CZE");
        assertThat(borderCrossing.route().get(1)).isEqualTo("AUT");
        assertThat(borderCrossing.route().get(2)).isEqualTo("ITA");

    }

    @Test
    public void shouldQueryBorderCrossingNotReturnItens(){
        Mono<BorderCrossing> result = countryRepository.queryBorderCrossing("XYZ", "ZZA");

        Assertions.assertThrows(NoLandingCrossesException.class, () -> {
            BorderCrossing borderCrossing = result.block();
        });

    }

    @AfterAll
    public static void tearDown(){
        mongoDBContainer.stop();
    }

}
