package com.monitoring.common;

public sealed interface Reading permits Reading.Scalar, Reading.Location {

    String toPayload();

    record Scalar(double value) implements Reading {
        @Override
        public String toPayload() {
            return String.valueOf(value);
        }
    }

    record Location(double latitude, double longitude) implements Reading {
        public Location {
            if (latitude < -90 || latitude > 90) {
                throw new IllegalArgumentException("Latitude out of range: " + latitude);
            }
            if (longitude < -180 || longitude > 180) {
                throw new IllegalArgumentException("Longitude out of range: " + longitude);
            }
        }

        @Override
        public String toPayload() {
            return latitude + "," + longitude;
        }
    }

    static Reading fromPayload(SensorType type, String payload) {
        if (type != SensorType.LOCATION) {
            return new Scalar(Double.parseDouble(payload));
        }
        String[] parts = payload.split(",");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Unexpected location payload: " + payload);
        }
        return new Location(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]));
    }
}
