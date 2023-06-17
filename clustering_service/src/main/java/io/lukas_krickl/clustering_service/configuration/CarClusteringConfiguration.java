package io.lukas_krickl.clustering_service.configuration;

import io.lukas_krickl.clustering_service.model.ClusterableCar;
import io.lukas_krickl.clustering_service.service.HaversineDistanceMeasurer;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import org.apache.commons.math3.ml.clustering.Clusterer;
import org.apache.commons.math3.ml.clustering.DBSCANClusterer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@Validated
@Data
@ConfigurationProperties("application.clustering")
public class CarClusteringConfiguration {
  @Positive
  private int minPoints = 1;
  @PositiveOrZero
  private double eps = 200D;
  @NotEmpty
  @Value("${application.clients.car-service.base-url}")
  private String carService;

  @Bean
  public WebClient configureWebclient(WebClient.Builder builder) {
    return builder.baseUrl(carService).build();
  }

  @Bean
  public Clusterer<ClusterableCar> getClusteringImplementation() {
    return new DBSCANClusterer<>(eps, minPoints, new HaversineDistanceMeasurer());
  }
}
