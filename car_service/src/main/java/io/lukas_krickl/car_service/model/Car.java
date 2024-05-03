package io.lukas_krickl.car_service.model;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class Car{
  private final String id;
  private final Position position;
  private final String model;
  private final PropulsionType propulsionType;
  private final FuelType fuelType;
  private final Transmission transmission;
  private String plate;
}
