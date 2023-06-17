package io.lukas_krickl.car_service.model;

import lombok.Builder;

@Builder
public record Car(
  String id,
  Position position,
  String model,
  PropulsionType propulsionType,
  FuelType fuelType,
  Transmission transmission,
  String plate
) {}
