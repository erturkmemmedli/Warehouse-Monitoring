package com.monitoring.common;

public record Measurement(String warehouseId, SensorType type, String sensorId, Reading reading) {

    public Measurement {
        boolean isLocation = reading instanceof Reading.Location;
        if (isLocation != (type == SensorType.LOCATION)) {
            throw new IllegalArgumentException(type + " cannot hold " + reading);
        }
    }

    public Measurement(String warehouseId, SensorType type, String sensorId, double value) {
        this(warehouseId, type, sensorId, new Reading.Scalar(value));
    }

    public String topic() {
        return "warehouse/%s/%s/%s".formatted(
            warehouseId,
            type.name().toLowerCase(),
            sensorId
        );
    }

    public String payload() {
        return reading.toPayload();
    }

    public static Measurement fromMqtt(String topic, String payload) {
        String[] parts = topic.split("/");
        if (parts.length != 4) {
            throw new IllegalArgumentException("Unexpected topic: " + topic);
        }
        String warehouseId = parts[1];
        SensorType type = SensorType.valueOf(parts[2].toUpperCase());
        String sensorId = parts[3];
        Reading reading = Reading.fromPayload(type, payload);
        return new Measurement(warehouseId, type, sensorId, reading);
    }
}
