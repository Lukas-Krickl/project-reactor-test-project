package io.lukas_krickl.car_service.integration_tests;

import io.lukas_krickl.car_service.configuration.CarRepositoryConfiguration;
import io.lukas_krickl.car_service.model.Car;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriBuilder;

import java.net.URI;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integrationTest")
class CarRestControllerTest {
  @Autowired
  CarRepositoryConfiguration configuration;
  @Autowired
  WebTestClient webTestClient;

  @Test
  @DisplayName("It should be possible to request all cars")
  void getAllCars() {
    webTestClient.get()
      .uri(uriBuilder -> uriBuilder.path("/cars").build())
      .exchange()
      .expectStatus()
      .isOk()
      .expectBodyList(Car.class)
      .hasSize(20);
  }

  @Test
  @DisplayName("It should be possible to request a car by its id")
  void getSpecificCar() {
    //given
    Function<UriBuilder, URI> uri = uriBuilder -> uriBuilder.path("/cars/{id}").build(1);

    //when
    var actualCar = webTestClient.get()
      .uri(uri)
      .exchange()
      .expectStatus()
      .isOk()
      .expectBody(Car.class)
      .returnResult()
      .getResponseBody();

    //then
    assertNotNull(actualCar);
    assertNotNull(actualCar.id());
    assertNotNull(actualCar.model());
    assertNotNull(actualCar.fuelType());
    assertNotNull(actualCar.plate());
    assertNotNull(actualCar.position());
    assertNotNull(actualCar.propulsionType());
    assertNotNull(actualCar.transmission());
  }
}
