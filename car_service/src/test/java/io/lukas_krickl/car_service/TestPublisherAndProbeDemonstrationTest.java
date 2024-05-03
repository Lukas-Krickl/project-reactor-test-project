package io.lukas_krickl.car_service;

import io.lukas_krickl.car_service.model.*;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.test.publisher.PublisherProbe;
import reactor.test.publisher.TestPublisher;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TestPublisherAndProbeDemonstrationTest {

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


  @Test
  void testPublisherForMocking() {
    //given
    var testCars = List.of(getTestCarWithId("1"), getTestCarWithId("2"));

    TestPublisher<Car> testPublisher = TestPublisher.<Car>create();
  }

  private static Car getTestCarWithId(String id) {
    return new Car(
      id,
      new Position(46.123, 15.123),
      "Reliant Robin",
      PropulsionType.COMBUSTION,
      FuelType.GASOLINE,
      Transmission.MANUAL,
      "H-1234"
    );
  }


  @Test
  @DisplayName("Extending cars with licence plates should call APIs only once")
  void usePublisherProbeToDetectRedundantSubscriptions() {
    //given
    var licencePlateClientMock = Mockito.mock(LicencePlateApiClient.class);
    var licencePlateExtensionService = new LicencePlateExtensionService(licencePlateClientMock);

    var mockedLicencePlateResponse = Mono.just(Map.of(
      "1", "AB-1234",
      "2", "AB-1224"
    ));
    var licencePlateProbe = PublisherProbe.of(mockedLicencePlateResponse);
    when(licencePlateClientMock.getCarIdToLicencePlates()).thenReturn(licencePlateProbe.mono());
    var carRepoProbe = PublisherProbe.of(Flux.just(getTestCarWithId("1"), getTestCarWithId("2")));

    //when
    licencePlateExtensionService.extendCarsWithLicencePlate(carRepoProbe.flux())
      .as(StepVerifier::create)
      .expectNextCount(2)
      .verifyComplete();

    //then
    assertThat(licencePlateProbe.subscribeCount()).isEqualTo(1);
    assertThat(carRepoProbe.subscribeCount()).isEqualTo(1);
  }

  @RequiredArgsConstructor
  static class LicencePlateExtensionService {
    private final LicencePlateApiClient plateClient;

    public Flux<Car> extendCarsWithLicencePlate(Flux<Car> cars) {
      return Flux.zip(
        cars,
        plateClient.getCarIdToLicencePlates(),
        (car, plateMap) -> {
          car.setPlate(plateMap.get(car.getId()));
          return car;
        });
    }
  }

  static class LicencePlateApiClient {
    public Mono<Map<String, String>> getCarIdToLicencePlates() {
      return Mono.just(Map.of(
        "1", "AB-1234",
        "2", "AB-1224"
      ));
    }
  }
}
