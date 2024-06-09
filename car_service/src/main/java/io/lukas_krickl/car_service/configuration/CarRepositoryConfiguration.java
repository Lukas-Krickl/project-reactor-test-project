package io.lukas_krickl.car_service.configuration;

import io.lukas_krickl.car_service.model.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

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

  private static final Random rand = new Random();

  @Bean("MockCarRepoDataSource")
  public Map<String, Car> createMockDataStore() {
    ConcurrentMap<String, Car> dataStore = new ConcurrentHashMap<>();
    for (int i = 0; i < mockGenerator.getAmountOfCars(); i++) {
      String carId = Integer.toString(i);
      dataStore.put(carId, generateMockCar(carId, mockGenerator.getModelNames()));
    }
    return dataStore;
  }

  private Car generateMockCar(String id, List<String> carModelNames) {
    return Car.builder()
      .id(id)
      .position(getRandomPosition())
      .model(getRandom(carModelNames))
      .propulsionType(getRandomPropulsionType())
      .fuelType(getRandomFuelType())
      .transmission(getRandomTransmission())
      .plate(getRandomPlate())
      .build();
  }

  private Position getRandomPosition() {
    return new Position(48.0 + rand.nextFloat(), 16.0 + rand.nextFloat());
  }

  private String getRandom(List<String> strings) {
    return strings.get(rand.nextInt(0, strings.size()));
  }

  private PropulsionType getRandomPropulsionType() {
    return PropulsionType.values()[rand.nextInt(0, PropulsionType.values().length)];
  }

  private FuelType getRandomFuelType() {
    return FuelType.values()[rand.nextInt(0, FuelType.values().length)];
  }

  private Transmission getRandomTransmission() {
    return Transmission.values()[rand.nextInt(0, Transmission.values().length)];
  }

  private String getRandomPlate() {
    return "W-" + rand.nextInt(1000, 99999);
  }

  @Valid
  @Data
  public static class MockGeneratorConfiguration {
    @Positive
    private int amountOfCars = 100;
    @NotEmpty
    private List<String> modelNames = List.of("Peugeot 305", "BMW F40", "Audi A4", "Suzuki Swift");
  }
}
