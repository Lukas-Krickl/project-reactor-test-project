package io.lukas_krickl.car_service;

import io.lukas_krickl.car_service.model.Position;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static java.lang.Math.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GeoUtils {
  private static final int EARTH_RADIUS_IN_METERS = 6_371_000;
  /*
   * Distance between coordinates using the haversine formula from: https://www.movable-type.co.uk/scripts/latlong.html
   */
  public static Double getDistanceInMeters(Position p1, Position p2){
    double phi1 = p1.lat() * PI/180;
    double phi2 = p2.lat() * PI/180;
    double deltaPhi = (p2.lat() - p1.lat()) * PI/180;
    double deltaLambda = (p2.lon() - p1.lon()) * PI/180;
    double haversine = pow(sin(deltaPhi/2), 2)
      + cos(phi1) * cos(phi2) * pow(sin(deltaLambda/2), 2);
    return abs(2 * atan2(sqrt(haversine), sqrt(1-haversine)) * EARTH_RADIUS_IN_METERS);
  }

}
