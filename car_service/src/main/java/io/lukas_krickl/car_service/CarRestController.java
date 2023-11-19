package io.lukas_krickl.car_service;

import io.lukas_krickl.car_service.model.Car;
import io.lukas_krickl.car_service.model.Circle;
import io.lukas_krickl.car_service.model.Position;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static io.lukas_krickl.car_service.error_handling.ErrorResponseExceptionFactory.createBadRequestResponse;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CarRestController {
  private final CarService service;

  public Mono<ServerResponse> getCars(ServerRequest request) {
    return parseRadialSearchQuery(request.queryParam("center"), request.queryParam("radius"))
      .map(service::getCarsWithinCenterAndRadius)
      .orElse(service.getCars())
      .transform(resp -> ServerResponse.ok().body(resp, Car.class))
      .single();
  }

  private Optional<Circle> parseRadialSearchQuery(Optional<String> centerQuery, Optional<String> radiusQuery) {
    if (centerQuery.isPresent() && radiusQuery.isPresent()) {
      try {
        int radius = Integer.parseInt(radiusQuery.get());
        if (radius <= 0) {
          throw new NumberFormatException();
        }
        return Optional.of(new Circle(
          Position.of(centerQuery.get()),
          radius
        ));
      } catch (NumberFormatException e) {
        throw createBadRequestResponse("radius", "query", "must be a positive integer", e);
      } catch (IllegalArgumentException e) {
        throw createBadRequestResponse(e.getMessage(), e);
      }
    } else if (centerQuery.isEmpty() && radiusQuery.isEmpty()) {
      return Optional.empty();
    }
    throw createBadRequestResponse("Radial filter must be provided by query params 'center' and 'radius'", null);
  }

  public Mono<ServerResponse> getCarById(ServerRequest request) {
    return ServerResponse.ok().body(service.getCar(request.pathVariable("id")), Car.class);
  }
}
