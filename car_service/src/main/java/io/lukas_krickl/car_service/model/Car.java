package io.lukas_krickl.car_service.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Locale;

@Builder
@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
public class Car {
  private final String id;
  private final Position position;
  private final String model;
  private final PropulsionType propulsionType;
  private final FuelType fuelType;
  private final Transmission transmission;
  private final String plate;
  @Builder.Default
  private Status status = Status.UNKNOWN;

  @JsonFormat(shape = JsonFormat.Shape.STRING)
  public enum Status {
    @JsonProperty("available")
    AVAILABLE,
    @JsonProperty("rented")
    RENTED,
    @JsonProperty("unknown")
    UNKNOWN;

    @JsonCreator
    public static Status of(@JsonProperty String status) {
      var enumName = status.toUpperCase(Locale.ENGLISH);
      return Status.valueOf(enumName);
    }
  }
}
