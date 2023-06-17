package io.lukas_krickl.clustering_service.service;

import io.lukas_krickl.clustering_service.model.CarClusteringResponse;
import io.lukas_krickl.clustering_service.model.ClusterableCar;
import io.lukas_krickl.clustering_service.model.car_service_response.Car;
import io.lukas_krickl.clustering_service.model.car_service_response.Position;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.math3.ml.clustering.Cluster;

import java.util.Arrays;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Mapper {
  public static Position toPosition(double[] coordinates) {
    if (coordinates.length != 2) {
      throw new IllegalArgumentException("could not create position from: " + Arrays.toString(coordinates));
    }
    return new Position(coordinates[0], coordinates[1]);
  }

  public static ClusterableCar mapToClusterableCar(Car car) {
    return new ClusterableCar(car.id(), car.position());
  }

  public static CarClusteringResponse mapToCarClusteringResponse(List<Cluster<ClusterableCar>> carClusters) {
    var mappedClusters = carClusters.stream()
      .map(Mapper::mapToCarCluster)
      .toList();
    return new CarClusteringResponse(mappedClusters);
  }

  private static CarClusteringResponse.CarCluster mapToCarCluster(Cluster<ClusterableCar> cluster) {
    var cars = cluster.getPoints()
      .stream()
      .map(Mapper::mapToCar)
      .toList();
    return new CarClusteringResponse.CarCluster(cars);
  }

  private static CarClusteringResponse.Car mapToCar(ClusterableCar clusterableCar) {
    return new CarClusteringResponse.Car(clusterableCar.id(), clusterableCar.position());
  }
}
