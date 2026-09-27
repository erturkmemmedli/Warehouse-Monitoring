package com.monitoring.warehouse;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.monitoring.common.Measurement;
import com.monitoring.common.SensorType;

class MeasurementParserTest {
    @Test
    void parsesValidMessage() {
        Optional<Measurement> result = MeasurementParser.parse(
            "sensor_id=t1; value=30", 
            "w1", 
            SensorType.TEMPERATURE
        );
        Optional<Measurement> measurement = Optional.of(new Measurement(
            "w1", 
            SensorType.TEMPERATURE, 
            "t1", 
            30
        ));
        assertEquals(measurement, result);
    }

    @Test
    void parsesDecimalMessage() {
        Optional<Measurement> result = MeasurementParser.parse(
            "sensor_id=h1; value=-2.5\n", 
            "w1", 
            SensorType.HUMIDITY
        );
        Optional<Measurement> measurement = Optional.of(new Measurement(
            "w1",
            SensorType.HUMIDITY, 
            "h1", 
            -2.5
        ));
        assertEquals(measurement, result);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "", 
        "hello", 
        "sensor_id=t1", 
        "value=30", 
        "sensor_id=t1; value=abc", 
        "sensor_id=; value=30"
    })
    void rejectsMalformedMessage(String raw) {
        assertTrue(MeasurementParser.parse(
            raw, 
            "w1", 
            SensorType.TEMPERATURE
        ).isEmpty());
    }
}