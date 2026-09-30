package com.monitoring.central;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.monitoring.common.Measurement;
import com.monitoring.common.SensorType;

class ThresholdMonitorTest {

    private final List<Alarm> alarms = new ArrayList<>();
    private final ThresholdMonitor monitor = new ThresholdMonitor(
        Map.of(
            SensorType.TEMPERATURE, 35.0,
            SensorType.HUMIDITY, 50.0
        ),
        List.of(alarms::add)
    );

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

    @Test
    void sendsAlarmToNotifiers() {
        Measurement measurement = new Measurement(
            "w1",
            SensorType.TEMPERATURE,
            "t1",
            40
        );
        monitor.check(measurement);
        assertEquals(List.of(new Alarm(measurement, 35.0)), alarms);
    }

    @Test
    void sendsNothingWhenOk() {
        monitor.check(new Measurement(
            "w1",
            SensorType.TEMPERATURE,
            "t1",
            30
        ));
        assertTrue(alarms.isEmpty());
    }
}