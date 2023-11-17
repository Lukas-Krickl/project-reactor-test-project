package io.lukas_krickl.car_service.integration_tests;

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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integrationTest")
public class Http2Test {
  @Autowired
  WebTestClient webTestClient;

  @Test
  @DisplayName("It should be possible to request a car by its id")
    //TODO Run to try the .log() operator
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
    assertEquals("1", actualCar.id());
    assertNotNull(actualCar.model());
    assertNotNull(actualCar.fuelType());
    assertNotNull(actualCar.plate());
    var position = actualCar.position();
    assertNotNull(position);
    assertNotNull(position.lat());
    assertNotNull(position.lon());
    assertNotNull(actualCar.propulsionType());
    assertNotNull(actualCar.transmission());
  }
}
