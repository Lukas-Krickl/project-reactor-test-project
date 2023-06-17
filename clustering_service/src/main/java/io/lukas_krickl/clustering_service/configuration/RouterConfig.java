package io.lukas_krickl.clustering_service.configuration;

import io.lukas_krickl.clustering_service.controller.CarClusteringRestController;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Component
@RequiredArgsConstructor
public class RouterConfig {
  private final CarClusteringRestController controller;

  @Bean
  RouterFunction<ServerResponse> routes() {
    return route()
      .GET("/cars/clusters", controller::getCarClusters)
      .GET("/cars/{id}", controller::getCarById)
      .GET("/cars", controller::getCars)
      .build();
  }
}
