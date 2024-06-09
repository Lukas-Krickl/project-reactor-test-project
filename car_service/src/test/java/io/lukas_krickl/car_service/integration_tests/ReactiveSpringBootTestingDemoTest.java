package io.lukas_krickl.car_service.integration_tests;

import io.lukas_krickl.car_service.CarRepository;
import io.lukas_krickl.car_service.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.test.publisher.PublisherProbe;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integrationTest")
class ReactiveSpringBootTestingDemoTest {
  @Autowired
  WebTestClient webTestClient;

  @MockBean
  CarRepository carRepositoryMock;

  @Test
  @DisplayName("It should be possible to request all cars (WebTestClient assertion)")
  void testGetAllCarsWithWebTestClient() {
    //given
    var allCarsResponseProbe = PublisherProbe.of(Flux.fromIterable(getTestCars()));
    when(carRepositoryMock.getCars()).thenReturn(allCarsResponseProbe.flux());

    webTestClient.get()
      .uri(uriBuilder -> uriBuilder.path("/cars").build())
      //when
      .exchange()
      //then
      .expectStatus()
      .isOk()
      .expectBodyList(Car.class)
      .hasSize(2);

    allCarsResponseProbe.wasSubscribed();
    assertThat(allCarsResponseProbe.subscribeCount()).isEqualTo(1L);
  }

  @Test
  @DisplayName("It should be possible to request all cars (StepVerifier assertion)")
  void testGetAllCarsWithStepVerifier() {
    //given
    when(carRepositoryMock.getCars()).thenReturn(Flux.fromIterable(getTestCars()));
    webTestClient.get()
      .uri(uriBuilder -> uriBuilder.path("/cars").build())
      //when
      .exchange()
      //then
      .expectStatus()
      .isOk()
      .returnResult(Car.class)
      .getResponseBody()
      .as(StepVerifier::create)
      .expectNextCount(2)
      .verifyComplete();
  }

  private List<Car> getTestCars() {
    return List.of(
      Car.builder()
        .id("1")
        .position(new Position(48.123, 16.342))
        .model("Porsche 911")
        .propulsionType(PropulsionType.COMBUSTION)
        .transmission(Transmission.MANUAL)
        .fuelType(FuelType.GASOLINE)
        .plate("W-111")
        .build(),
      Car.builder()
        .id("2")
        .position(new Position(48.113, 16.242))
        .model("Audi S2")
        .propulsionType(PropulsionType.COMBUSTION)
        .transmission(Transmission.MANUAL)
        .fuelType(FuelType.GASOLINE)
        .plate("W-222")
        .build()
    );
  }
}
