package io.lukas_krickl.car_service.model;

import java.util.Arrays;
import java.util.regex.Pattern;

public record Position(Double lat, Double lon) {
  static final Pattern COORDINATES_FORMAT = Pattern.compile("^-?\\d{1,2}\\.\\d{1,10}, *-?\\d{1,2}\\.\\d{1,10}");

  public static Position of(String s) {
    if (!COORDINATES_FORMAT.matcher(s).matches()) {
      throw new IllegalArgumentException("Position does not match required format:'"+ COORDINATES_FORMAT.pattern() +"'");
    }
    var coordinates = Arrays.stream(s.split(","))
      .map(String::strip)
      .map(Double::parseDouble)
      .toList();
    return new Position(coordinates.get(0), coordinates.get(1));
  }
}
