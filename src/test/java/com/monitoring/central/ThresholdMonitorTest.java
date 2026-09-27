package com.monitoring.central;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.monitoring.common.Measurement;
import com.monitoring.common.SensorType;

class ThresholdMonitorTest {

    private final ThresholdMonitor monitor = new ThresholdMonitor(Map.of(
        SensorType.TEMPERATURE, 35.0,
        SensorType.HUMIDITY, 50.0
    ));

    @Test
    void alarmsOnHighTemperature() {
        assertTrue(monitor.check(new Measurement(
            "w1", 
            SensorType.TEMPERATURE, 
            "t1", 
            35.1
        )));
    }

    @Test
    void alarmsOnHighHumidity() {
        assertTrue(monitor.check(new Measurement(
            "w1",
            SensorType.HUMIDITY, 
            "h1", 
            51
        )));
    }

    @Test
    void noAlarmAtThreshold() {
        assertFalse(monitor.check(new Measurement(
            "w1", 
            SensorType.TEMPERATURE, 
            "t1", 
            35
        )));
    }

    @Test
    void noAlarmBelowThreshold() {
        assertFalse(monitor.check(new Measurement(
            "w1", 
            SensorType.HUMIDITY, 
            "h1", 
            40
        )));
    }
}