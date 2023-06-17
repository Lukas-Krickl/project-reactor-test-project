package io.lukas_krickl.car_service.configuration;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;
import java.util.List;

@Configuration
@ConfigurationProperties("application.car-repository")
@Validated
@Data
@Accessors(chain = true)
public class CarRepositoryConfiguration {
  @NotNull
  private Duration workFactor = Duration.ZERO;
  @NotNull
  private MockGeneratorConfiguration mockGenerator = new MockGeneratorConfiguration();

  @Valid
  @Data
  public static class MockGeneratorConfiguration {
    @Positive
    private int amountOfCars = 100;
    @NotEmpty
    private List<String> modelNames = List.of("Peugeot 305", "BMW F40", "Audi A4", "Suzuki Swift");
  }
}
