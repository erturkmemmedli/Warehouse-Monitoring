package com.monitoring.common;

public record Measurement(String warehouseId, SensorType type, String sensorId, double value) {

    public String topic() {
        return "warehouse/%s/%s/%s".formatted(
            warehouseId, 
            type.name().toLowerCase(), 
            sensorId
        );
    }

    public static Measurement fromMqtt(String topic, String payload) {
        String[] parts = topic.split("/");
        if (parts.length != 4) {
            throw new IllegalArgumentException("Unexpected topic: " + topic);
        }
        String warehouseId = parts[1];
        SensorType type = SensorType.valueOf(parts[2].toUpperCase());
        String sensorId = parts[3];
        double payloadVal = Double.parseDouble(payload);
        return new Measurement(warehouseId, type, sensorId, payloadVal);
    }
}