package io.lukas_krickl.car_service.configuration;

import io.lukas_krickl.car_service.CarRestController;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@RequiredArgsConstructor
public class RouterConfig {
  private final CarRestController carRestcontroller;

  @Bean
  RouterFunction<ServerResponse> routes() {
    return route()
      .GET("/cars", carRestcontroller::getCars)
      .GET("/cars/{id}", carRestcontroller::getCarById)
      .build();
  }
}
