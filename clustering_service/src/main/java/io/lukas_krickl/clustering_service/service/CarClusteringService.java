package io.lukas_krickl.clustering_service.service;

import io.lukas_krickl.clustering_service.model.ClusterableCar;
import io.lukas_krickl.clustering_service.model.car_service_response.Car;
import lombok.RequiredArgsConstructor;
import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.Clusterer;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CarClusteringService {
  private final WebClient webClient;
  private final Clusterer<ClusterableCar> clusterer;

  public Flux<Cluster<ClusterableCar>> getCarClusters() {
    return requestAllCars().map(Mapper::mapToClusterableCar)
      .collectList()
      .flatMapIterable(clusterer::cluster);
  }

  public Flux<Car> getCars() {
    return webClient.get()
      .uri(uriBuilder -> uriBuilder.path("/cars").build())
      .exchangeToFlux(clientResponse -> !clientResponse.statusCode().is2xxSuccessful() ?
        clientResponse.<Car>createError().flux()
        : clientResponse.bodyToFlux(Car.class));
  }

  public Mono<Car> getCar(String id) {
    return webClient.get()
      .uri(uriBuilder -> uriBuilder.path("/cars/{id}").build(id))
      .exchangeToMono(clientResponse -> clientResponse.bodyToMono(Car.class));
  }

  private Flux<Car> requestAllCars() {
    return webClient.get()
      .uri(uriBuilder -> uriBuilder.path("/cars").build())
      .exchangeToFlux(clientResponse -> clientResponse.bodyToFlux(Car.class));
  }
}
