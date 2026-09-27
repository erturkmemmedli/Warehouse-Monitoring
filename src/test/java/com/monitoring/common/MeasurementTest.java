package com.monitoring.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

class MeasurementTest {

    @Test
    void buildsTopic() {
        Measurement measurement = new Measurement(
            "w1", 
            SensorType.TEMPERATURE, 
            "t1", 
            30
        );
        assertEquals("warehouse/w1/temperature/t1", measurement.topic());
    }

    @Test
    void restoresFromMqtt() {
        Measurement original = new Measurement(
            "w1", 
            SensorType.HUMIDITY, 
            "h1", 
            40.5
        );
        assertEquals(original, Measurement.fromMqtt(original.topic(), "40.5"));
    }

    @Test
    void rejectsBadTopic() {
        assertThrows(
            IllegalArgumentException.class, 
            () -> Measurement.fromMqtt("warehouse/w1", "30")
        );
    }

    @Test
    void rejectsBadPayload() {
        assertThrows(
            IllegalArgumentException.class, 
            () -> Measurement.fromMqtt("warehouse/w1/temperature/t1", "hot")
        );
    }
}