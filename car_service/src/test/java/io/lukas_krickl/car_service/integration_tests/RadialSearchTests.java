package io.lukas_krickl.car_service.integration_tests;

import io.lukas_krickl.car_service.model.Car;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Hooks;

import java.net.URI;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integrationTest")
class RadialSearchTests {
  @Autowired
  WebTestClient webTestClient;

  @Test
  @DisplayName("It should be possible to request cars by center and radius")
  void getCarsByCenterAndRadius() {
    //Hooks.onOperatorDebug(); //TODO enable to try the reactor debug agent
    //given
    Function<UriBuilder, URI> uri = uriBuilder -> uriBuilder.path("/cars")
      .queryParam("center", "48.5,16.5")
      .queryParam("radius", "70000")
      .build(1);

    //when && then
    Consumer<Car> assertCarPropertiesNotNull = actualCar -> {
      assertNotNull(actualCar);
      assertNotNull(actualCar.getId());
      assertNotNull(actualCar.getModel());
      assertNotNull(actualCar.getFuelType());
      assertNotNull(actualCar.getPlate());
      assertNotNull(actualCar.getPosition());
      assertNotNull(actualCar.getPropulsionType());
      assertNotNull(actualCar.getTransmission());
    };

    webTestClient.get()
      .uri(uri)
      .exchange()
      .expectStatus()
      .isOk()
      .expectBodyList(Car.class)
      .value(cars -> assertThat(cars).isNotEmpty().allSatisfy(assertCarPropertiesNotNull));
  }

  @Test
  @DisplayName("Radius query should be validated")
  void getCarsByCenterAndRadiusWithNegativeRadius() {
    //Hooks.onOperatorDebug(); //TODO enable to try the reactor debug agent
    //given
    Function<UriBuilder, URI> uri = uriBuilder -> uriBuilder.path("/cars")
      .queryParam("center", "48.5,16.5")
      .queryParam("radius", "0")
      .build(1);

    //when && then
    webTestClient.get()
      .uri(uri)
      .exchange()
      .expectStatus()
      .isBadRequest();
  }
}
