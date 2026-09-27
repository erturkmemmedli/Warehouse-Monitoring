package com.monitoring.warehouse;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.monitoring.common.Measurement;
import com.monitoring.common.SensorType;

public final class MeasurementParser {

    private static final Pattern DATA_FORMAT = Pattern.compile(
        "sensor_id=([\\w-]+); value=(-?\\d+(\\.\\d+)?)"
    );

    private MeasurementParser() {}

    public static Optional<Measurement> parse(String raw, String warehouseId, SensorType type) {
        Matcher matcher = DATA_FORMAT.matcher(raw.trim());
        if (!matcher.matches()) {
            return Optional.empty();
        }
        double value = Double.parseDouble(matcher.group(2));
        String sensorId = matcher.group(1);
        return Optional.of(new Measurement(warehouseId, type, sensorId, value));
    }
}