package io.lukas_krickl.car_service;

import io.lukas_krickl.car_service.configuration.CarRepositoryConfiguration;
import io.lukas_krickl.car_service.model.Car;
import io.micrometer.observation.ObservationRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import reactor.core.observability.micrometer.Micrometer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;

import java.util.Map;

@Component
@Slf4j
public class CarRepository {
  private final ObservationRegistry observationRegistry;
  private final CarRepositoryConfiguration config;
  private final Map<String, Car> dataStore;

  public CarRepository(CarRepositoryConfiguration config, @Qualifier("MockCarRepoDataSource") Map<String, Car> dataStore, ObservationRegistry observationRegistry) {
    this.config = config;
    this.dataStore = dataStore;
    this.observationRegistry = observationRegistry;
  }

  public Flux<Car> getCars() {
    var sink = Sinks.many().replay().<Car>all();
    Flux.fromIterable(dataStore.values())
      .delayElements(config.getWorkFactor())
      .doFinally(s -> sink.tryEmitComplete())
      .subscribeOn(Schedulers.boundedElastic())
      .subscribe(sink::tryEmitNext);
    return sink.asFlux()
      .name("car_repository")
      .tag("query", "getAllCars")
      .tap(Micrometer.observation(observationRegistry));
  }

  public Mono<Car> getCarById(String id) {
    return Mono.justOrEmpty(dataStore.get(id))
      .delayElement(config.getWorkFactor())
      .name("car_repository")
      .tag("query", "getCarById")
      .tap(Micrometer.observation(observationRegistry));
  }
}
