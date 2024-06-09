package io.lukas_krickl.car_service;

import io.lukas_krickl.car_service.configuration.CarRepositoryConfiguration;
import io.lukas_krickl.car_service.model.*;
import io.micrometer.observation.ObservationRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.Fuseable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class StepVerifierDemoTest {
  private CarRepository carRepository;
  private Map<String, Car> dataStore;

  @BeforeEach
  void setUp() {
    dataStore = new HashMap<>();
    CarRepositoryConfiguration carRepositoryConfiguration = new CarRepositoryConfiguration();
    carRepository = new CarRepository(carRepositoryConfiguration, dataStore, Mockito.mock(ObservationRegistry.class));
  }

  @Test
  void expectNextElement() {
    //given
    Car car = getExampleCar();

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
    Flux<String> exampleElements = Flux.fromIterable(List.of("a", "b", "c"));

    //when
    exampleElements.as(StepVerifier::create)
      .expectNext("a")
      .expectNext("b")
      .expectNext("c")
      .verifyComplete();

    //or
    exampleElements.as(StepVerifier::create)
      .expectNext("a", "b", "c")
      .verifyComplete();
  }

  @Test
  @DisplayName("When a car is requested by its id, the correct car with the requested id is returned")
  void assertNextElements() {
    //given
    var testCar = getExampleCar();
    dataStore.put(testCar.getId(), testCar);

    //when
    carRepository.getCarById(testCar.getId())
      .as(StepVerifier::create)
      //then
      .assertNext(car -> {
        assertThat(car).isNotNull();
        assertThat(car.getId()).isEqualTo(testCar.getId());
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

  private static Car getExampleCar() {
    return new Car(
      "1",
      new Position(46.123, 15.123),
      "Reliant Robin",
      PropulsionType.COMBUSTION,
      FuelType.GASOLINE,
      Transmission.MANUAL,
      "H-1234",
      Car.Status.UNKNOWN
    );
  }
}
