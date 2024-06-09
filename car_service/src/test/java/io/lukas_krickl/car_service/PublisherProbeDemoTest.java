package io.lukas_krickl.car_service;

import io.lukas_krickl.car_service.model.*;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.test.publisher.PublisherProbe;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class PublisherProbeDemoTest {

  @Test
  @DisplayName("Mockito can be used for mocking method results")
  void mockitoForMocking() {
    //given
    CarRepository carRepository = Mockito.mock(CarRepository.class);
    CarService carService = new CarService(carRepository);
    var testCars = List.of(getTestCarWithId("1"), getTestCarWithId("2"));
    when(carRepository.getCars()).thenReturn(Flux.fromIterable(testCars));

    //when
    carService.getCars()
      .as(StepVerifier::create)
      //then
      .expectNextSequence(testCars)
      .verifyComplete();
  }

  @Test
  @DisplayName("Mockito can be used for mocking method results")
  void publisherProbeForSignalAssertions() {
    //given
    CarRepository carRepository = Mockito.mock(CarRepository.class);
    CarService carService = new CarService(carRepository);
    var testCars = List.of(getTestCarWithId("1"), getTestCarWithId("2"));
    PublisherProbe<Car> carRepositoryProbe = PublisherProbe.of(Flux.fromIterable(testCars));
    when(carRepository.getCars()).thenReturn(carRepositoryProbe.flux());

    //when
    var carQueryWithCache = carService.getCars().cache();
    StepVerifier.create(carQueryWithCache)
      .expectNextCount(2)
      .verifyComplete();
    StepVerifier.create(carQueryWithCache)
      .expectNextCount(2)
      .verifyComplete();

    //then
    assertThat(carRepositoryProbe.subscribeCount()).isEqualTo(1);
  }

  private static Car getTestCarWithId(String id) {
    return new Car(
      id,
      new Position(46.123, 15.123),
      "Reliant Robin",
      PropulsionType.COMBUSTION,
      FuelType.GASOLINE,
      Transmission.MANUAL,
      "H-1234",
      Car.Status.UNKNOWN
    );
  }

  @Test
  @DisplayName("Extending cars with availability should call API only once")
  void usePublisherProbeToDetectRedundantSubscriptions() {
    //given
    var carAvailabilityApiClientMock = Mockito.mock(CarAvailabilityApiClient.class);
    var carAvailabilityService = new CarAvailabilityService(carAvailabilityApiClientMock);

    var mockedAvailabilityResponse = Mono.just(Map.of(
      "1", true,
      "2", false
    ));
    var availabilityProbe = PublisherProbe.of(mockedAvailabilityResponse);
    when(carAvailabilityApiClientMock.getCarIdToAvailability()).thenReturn(availabilityProbe.mono());
    var carRepoProbe = PublisherProbe.of(Flux.just(getTestCarWithId("1"), getTestCarWithId("2")));

    //when
    carAvailabilityService.setAvailability(carRepoProbe.flux())
      .as(StepVerifier::create)
      .expectNextCount(2)
      .verifyComplete();

    //then
    assertThat(availabilityProbe.subscribeCount()).isEqualTo(1);
    assertThat(carRepoProbe.subscribeCount()).isEqualTo(1);
  }

  @RequiredArgsConstructor
  static class CarAvailabilityService {
    private final CarAvailabilityApiClient availabilityApiClient;

    public Flux<Car> setAvailability(Flux<Car> cars) {
      var carAvailability = availabilityApiClient.getCarIdToAvailability().share();
      return cars.flatMap(car ->
        carAvailability.map(availabilityMap -> {
          car.setStatus(availabilityMap.get(car.getId()) ?
            Car.Status.AVAILABLE : Car.Status.RENTED);
          return car;
        })
      );
    }
  }

  static class CarAvailabilityApiClient {
    public Mono<Map<String, Boolean>> getCarIdToAvailability() {
      return Mono.just(Map.of(
        "1", true,
        "2", false
      ));
    }
  }
}
