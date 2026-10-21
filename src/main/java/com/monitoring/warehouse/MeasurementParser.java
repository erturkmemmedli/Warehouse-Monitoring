package com.monitoring.warehouse;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.monitoring.common.Measurement;
import com.monitoring.common.Reading;
import com.monitoring.common.SensorType;

public final class MeasurementParser {

    private static final String NUMBER = "(-?\\d+(?:\\.\\d+)?)";

    private static final Pattern DATA_FORMAT = Pattern.compile(
        "sensor_id=([\\w-]+); value=" + NUMBER
    );

    private static final Pattern LOCATION_FORMAT = Pattern.compile(
        "sensor_id=([\\w-]+); latitude=" + NUMBER + "; longitude=" + NUMBER
    );

    private MeasurementParser() {}

    public static Optional<Measurement> parse(String raw, String warehouseId, SensorType type) {
        Pattern format = type == SensorType.LOCATION ? LOCATION_FORMAT : DATA_FORMAT;
        Matcher matcher = format.matcher(raw.trim());
        if (!matcher.matches()) {
            return Optional.empty();
        }
        String sensorId = matcher.group(1);
        try {
            Reading reading = type == SensorType.LOCATION
                ? new Reading.Location(
                    Double.parseDouble(matcher.group(2)),
                    Double.parseDouble(matcher.group(3))
                )
                : new Reading.Scalar(Double.parseDouble(matcher.group(2)));
            return Optional.of(new Measurement(warehouseId, type, sensorId, reading));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
