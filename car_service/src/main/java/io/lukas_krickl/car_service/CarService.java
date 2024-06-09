package io.lukas_krickl.car_service;

import io.lukas_krickl.car_service.model.Car;
import io.lukas_krickl.car_service.model.Circle;
import io.lukas_krickl.car_service.model.Position;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Comparator;

@Service
@Slf4j
@RequiredArgsConstructor
public class CarService {
  private static final Comparator<CarAndDistance> SORT_DISTANCE_ASC = Comparator.comparing(CarAndDistance::distance);
  private final CarRepository carRepository;

  public Flux<Car> getCars() {
    return carRepository.getCars();
  }

  public Flux<Car> getCarsWithinCenterAndRadius(Circle radialFilter) {
    return carRepository.getCars()
      .map(car -> calculateDistance(car, radialFilter.center()))
      .filter(carAndDistance -> carAndDistance.distance <= radialFilter.radius())
      .sort(SORT_DISTANCE_ASC)
      .map(CarAndDistance::car);
  }

  private CarAndDistance calculateDistance(Car car, Position requestedPosition) {
    return new CarAndDistance(car, GeoUtils.getDistanceInMeters(car.getPosition(), requestedPosition));
  }

  public Mono<Car> getCar(String id) {
    return carRepository.getCarById(id);
  }

  private record CarAndDistance(Car car, double distance) {}
}
