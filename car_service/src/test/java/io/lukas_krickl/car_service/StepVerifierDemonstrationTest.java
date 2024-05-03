package io.lukas_krickl.car_service;

import io.lukas_krickl.car_service.configuration.CarRepositoryConfiguration;
import io.lukas_krickl.car_service.model.*;
import io.micrometer.observation.ObservationRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.Fuseable;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class StepVerifierDemonstrationTest {
  private CarRepositoryConfiguration carRepositoryConfiguration;
  private CarRepository carRepository;

  @BeforeEach
  void setUp() {
    carRepositoryConfiguration = new CarRepositoryConfiguration();
    carRepository = new CarRepository(carRepositoryConfiguration, Mockito.mock(ObservationRegistry.class));
  }

  @Test
  void expectNextElement() {
    //given
    Car car = new Car(
      "1",
      new Position(46.123, 15.123),
      "Reliant Robin",
      PropulsionType.COMBUSTION,
      FuelType.GASOLINE,
      Transmission.MANUAL,
      "H-1234"
    );

    //when
    Mono.just(car)
      .as(StepVerifier::create)
      //then
      .expectNext(car)
      .verifyComplete();
  }

  @Test
  void expectNextElements() {
    //given

    //when
    Mono.just(List.of("a", "b", "c"))
      .as(StepVerifier::create)
      //then
      .verifyComplete();
  }

  @Test
  @DisplayName("When a car is requested by its id, the correct car with the requested id is returned")
  void assertNextElements() {
    //when
    carRepository.getCarById("1")
      .as(StepVerifier::create)
      //then
      .assertNext(car -> {
        assertThat(car).isNotNull();
        assertThat(car.getId()).isEqualTo("1");
      });
  }

  @Test
  void testReactorOperatorFusion() {
    Mono.just("Some Data")
      .map(s -> s + " and some addition")
      .map(s -> s + " and another addition")
      .map(s -> s + " ending with this")
      .as(StepVerifier::create)
      .expectFusion(Fuseable.SYNC)
      .expectNextCount(1)
      .verifyComplete();
  }
}
