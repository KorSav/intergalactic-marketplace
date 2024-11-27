package com.example.intergalactic_marketplace;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static java.lang.String.format;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext
public abstract class AbstractIt {

  private static final int POSTGRES_PORT = 5432;

  static final GenericContainer<?> POSTGRES_CONTAINER =
      new GenericContainer<>("postgres:15.6-alpine")
          .withEnv("POSTGRES_PASSWORD", "postgres")
          .withEnv("POSTGRES_DB", "postgres")
          .withExposedPorts(POSTGRES_PORT);

  static final WireMockServer wireMockServer = new WireMockServer(wireMockConfig().dynamicPort());

  static {
    POSTGRES_CONTAINER.start();
    wireMockServer.start();
  }

  @DynamicPropertySource
  static void setupTestContainerProperties(DynamicPropertyRegistry registry) {
    registry.add(
        "spring.datasource.url",
        () ->
            format(
                "jdbc:postgresql://%s:%d/postgres",
                POSTGRES_CONTAINER.getHost(), POSTGRES_CONTAINER.getMappedPort(POSTGRES_PORT)));
    registry.add("spring.datasource.username", () -> "postgres");
    registry.add("spring.datasource.password", () -> "postgres");

    registry.add("wiremock.server.port", wireMockServer::port);
  }

  @AfterAll
  static void tearDown() {
    wireMockServer.stop();
  }
}
