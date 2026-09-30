package com.monitoring.central;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.monitoring.common.Measurement;
import com.monitoring.common.SensorType;

class SensorWatchdogTest {

    private final SensorWatchdog watchdog = new SensorWatchdog(Duration.ofSeconds(60));

    @Test
    void reportsSilentSensorOnce() {
        watchdog.record(new Measurement(
            "w1",
            SensorType.TEMPERATURE,
            "t1",
            30
        ));
        Instant later = Instant.now().plusSeconds(120);
        assertEquals(List.of("warehouse/w1/temperature/t1"), watchdog.findSilent(later));
        assertTrue(watchdog.findSilent(later).isEmpty());
    }

    @Test
    void ignoresSensorThatReportedRecently() {
        watchdog.record(new Measurement(
            "w1",
            SensorType.TEMPERATURE,
            "t1",
            30
        ));
        assertTrue(watchdog.findSilent(Instant.now()).isEmpty());
    }
}