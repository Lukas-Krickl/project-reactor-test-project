package io.lukas_krickl.car_service;

import io.lukas_krickl.car_service.model.*;
import io.lukas_krickl.car_service.configuration.CarRepositoryConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Component
@Slf4j
class CarRepository {
  private final Random rand = new Random();
  private final CarRepositoryConfiguration config;
  private final Map<String, Car> dataStore;

  public CarRepository(CarRepositoryConfiguration config) {
    this.config = config;
    this.dataStore = new HashMap<>(config.getMockGenerator().getAmountOfCars());
    for (int i = 0; i < config.getMockGenerator().getAmountOfCars(); i++) {
      String carId = Integer.toString(i);
      dataStore.put(carId, generateMockCar(carId));
    }
  }

  public Flux<Car> getCars() {
    var sink = Sinks.many().replay().<Car>all();
    Flux.fromIterable(dataStore.values())
      .delayElements(config.getWorkFactor())
      .doFinally(s -> sink.tryEmitComplete())
      .subscribeOn(Schedulers.boundedElastic())
      .subscribe(sink::tryEmitNext);
    return sink.asFlux();
  }

  public Mono<Car> getCarById(String id) {
    return Mono.justOrEmpty(dataStore.get(id))
      .delayElement(config.getWorkFactor());
  }

  private Car generateMockCar(String id) {
    return Car.builder()
      .id(id)
      .position(getRandomPosition())
      .model(getRandomModelName())
      .propulsionType(getRandomPropulsionType())
      .fuelType(getRandomFuelType())
      .transmission(getRandomTransmission())
      .plate(getRandomPlate())
      .build();
  }

  private Position getRandomPosition() {
    return new Position(48.0 + rand.nextFloat(), null);// //FIXME 16.0 + rand.nextFloat());
  }

  private String getRandomModelName() {
    List<String> modelNames = config.getMockGenerator().getModelNames();
    return modelNames.get(rand.nextInt(0, modelNames.size()));
  }

  private PropulsionType getRandomPropulsionType() {
    return PropulsionType.values()[rand.nextInt(0, PropulsionType.values().length)];
  }

  private FuelType getRandomFuelType() {
    return FuelType.values()[rand.nextInt(0, FuelType.values().length)];
  }

  private Transmission getRandomTransmission() {
    return Transmission.values()[rand.nextInt(0, Transmission.values().length)];
  }

  private String getRandomPlate() {
    return "W " + rand.nextInt(1000, 99999);
  }
}
