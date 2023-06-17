package io.lukas_krickl.clustering_service.model.car_service_response;

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
