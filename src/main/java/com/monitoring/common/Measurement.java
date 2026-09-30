package com.monitoring.common;

import java.time.Instant;
import java.time.format.DateTimeParseException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public record Measurement(String warehouseId, SensorType type, String sensorId, double value, Instant measuredAt) {

    private static final ObjectMapper JSON = new ObjectMapper();

    public Measurement(String warehouseId, SensorType type, String sensorId, double value) {
        this(warehouseId, type, sensorId, value, Instant.now());
    }

    public String topic() {
        return "warehouse/%s/%s/%s".formatted(
            warehouseId,
            type.name().toLowerCase(),
            sensorId
        );
    }

    public String payload() {
        return JSON.createObjectNode()
                .put("value", value)
                .put("measuredAt", measuredAt.toString())
                .toString();
    }

    public static Measurement fromMqtt(String topic, String payload) {
        String[] parts = topic.split("/");
        if (parts.length != 4) {
            throw new IllegalArgumentException("Unexpected topic: " + topic);
        }
        String warehouseId = parts[1];
        SensorType type = SensorType.valueOf(parts[2].toUpperCase());
        String sensorId = parts[3];
        try {
            JsonNode json = JSON.readTree(payload);
            double value = json.required("value").asDouble();
            Instant measuredAt = Instant.parse(json.required("measuredAt").asText());
            return new Measurement(warehouseId, type, sensorId, value, measuredAt);
        } catch (JsonProcessingException | DateTimeParseException e) {
            throw new IllegalArgumentException("Unexpected payload: " + payload, e);
        }
    }
}