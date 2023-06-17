package io.lukas_krickl.clustering_service.controller;

import io.lukas_krickl.clustering_service.model.car_service_response.Car;
import io.lukas_krickl.clustering_service.service.CarClusteringService;
import io.lukas_krickl.clustering_service.service.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class CarClusteringRestController {
  private final CarClusteringService service;

  public Mono<ServerResponse> getCarClusters(ServerRequest request) {
    return service.getCarClusters()
      .collectList()
      .map(Mapper::mapToCarClusteringResponse)
      .transform(response -> ServerResponse.ok().body(response, Car.class));
  }

  public Mono<ServerResponse> getCars(ServerRequest request) {
    return service.getCars()
      .collectList()
      .transform(response -> ServerResponse.ok().body(response, Car.class));
  }

  public Mono<ServerResponse> getCarById(ServerRequest request) {
    return service.getCar(request.pathVariable("id"))
      .transform(response -> ServerResponse.ok().body(response, Car.class));
  }
}
