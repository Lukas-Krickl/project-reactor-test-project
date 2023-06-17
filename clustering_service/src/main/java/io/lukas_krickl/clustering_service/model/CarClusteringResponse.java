package io.lukas_krickl.clustering_service.model;

import io.lukas_krickl.clustering_service.model.car_service_response.Position;

import java.util.List;

public record CarClusteringResponse(List<CarCluster> clusters) {
  public record CarCluster(List<Car> cars) {}

  public record Car(String id, Position position) {}
}
