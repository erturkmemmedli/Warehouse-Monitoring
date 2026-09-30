package com.monitoring.central;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.monitoring.common.Measurement;

public class HistoryStore {

    private static final Logger log = LoggerFactory.getLogger(HistoryStore.class);
    private final Path file;

    public HistoryStore(Path file) {
        this.file = file;
    }

    public synchronized void record(Measurement measurement) {
        String line = "%s,%s,%s,%s,%s%n".formatted(
            Instant.now(),
            measurement.warehouseId(),
            measurement.type(),
            measurement.sensorId(),
            measurement.value()
        );
        try {
            Files.writeString(
                file,
                line,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
            );
        } catch (IOException e) {
            log.error("Failed to store {}", measurement, e);
        }
    }
}