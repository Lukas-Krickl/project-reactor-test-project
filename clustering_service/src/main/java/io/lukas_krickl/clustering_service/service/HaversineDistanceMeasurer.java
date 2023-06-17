package io.lukas_krickl.clustering_service.service;

import io.lukas_krickl.clustering_service.GeoUtils;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.ml.distance.DistanceMeasure;

public class HaversineDistanceMeasurer implements DistanceMeasure {
  @Override
  public double compute(double[] a, double[] b) throws DimensionMismatchException {
    return GeoUtils.getDistanceInMeters(
      Mapper.toPosition(a),
      Mapper.toPosition(b)
    );
  }
}
