package io.lukas_krickl.clustering_service.model;

import io.lukas_krickl.clustering_service.model.car_service_response.Position;
import org.apache.commons.math3.ml.clustering.Clusterable;

public record ClusterableCar(
  String id,
  Position position
) implements Clusterable {
  @Override
  public double[] getPoint() {
    return new double[]{position.lat(), position.lon()};
  }
}
