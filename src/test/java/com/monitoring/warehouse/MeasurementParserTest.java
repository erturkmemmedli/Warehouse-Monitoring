package com.monitoring.warehouse;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.monitoring.common.Measurement;
import com.monitoring.common.Reading;
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

    @Test
    void parsesLocationMessage() {
        Optional<Measurement> result = MeasurementParser.parse(
            "sensor_id=l1; latitude=40.4093; longitude=-49.8671",
            "w1",
            SensorType.LOCATION
        );
        Optional<Measurement> measurement = Optional.of(new Measurement(
            "w1",
            SensorType.LOCATION,
            "l1",
            new Reading.Location(40.4093, -49.8671)
        ));
        assertEquals(measurement, result);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "sensor_id=l1; value=30",
        "sensor_id=l1; latitude=40.4",
        "sensor_id=l1; latitude=abc; longitude=49.8",
        "sensor_id=l1; latitude=91; longitude=49.8",
        "sensor_id=l1; latitude=40.4; longitude=181"
    })
    void rejectsMalformedLocationMessage(String raw) {
        assertTrue(MeasurementParser.parse(
            raw,
            "w1",
            SensorType.LOCATION
        ).isEmpty());
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