package io.lukas_krickl.car_service.model;

/**
 * @param center as position
 * @param radius radius in meters
 */
public record Circle(Position center, int radius) {}
