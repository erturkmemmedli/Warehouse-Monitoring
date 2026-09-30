package com.monitoring.central;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.monitoring.common.Measurement;
import com.monitoring.common.SensorType;

class HistoryStoreTest {

    @TempDir
    Path dir;

    @Test
    void appendsOneLinePerMeasurement() throws Exception {
        Path file = dir.resolve("history.csv");
        HistoryStore history = new HistoryStore(file);
        history.record(new Measurement(
            "w1",
            SensorType.TEMPERATURE,
            "t1",
            30
        ));
        history.record(new Measurement(
            "w1",
            SensorType.HUMIDITY,
            "h1",
            45
        ));

        List<String> lines = Files.readAllLines(file);
        assertEquals(2, lines.size());
        assertTrue(lines.get(0).endsWith(",w1,TEMPERATURE,t1,30.0"));
        assertTrue(lines.get(1).endsWith(",w1,HUMIDITY,h1,45.0"));
    }
}