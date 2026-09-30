package com.monitoring.central;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.monitoring.common.Measurement;

import reactor.core.publisher.Flux;

public class SensorWatchdog {

    private static final Logger log = LoggerFactory.getLogger(SensorWatchdog.class);
    private final Map<String, Instant> lastSeen = new ConcurrentHashMap<>();
    private final Duration timeout;

    public SensorWatchdog(Duration timeout) {
        this.timeout = timeout;
    }

    public void record(Measurement measurement) {
        lastSeen.put(measurement.topic(), Instant.now());
    }

    public void start() {
        Flux.interval(Duration.ofSeconds(10)).subscribe(tick -> findSilent(Instant.now()));
    }

    public List<String> findSilent(Instant now) {
        List<String> silent = new ArrayList<>();
        lastSeen.forEach((sensor, seen) -> {
            if (seen.plus(timeout).isBefore(now) && lastSeen.remove(sensor, seen)) {
                log.warn("Sensor {} silent since {}", sensor, seen);
                silent.add(sensor);
            }
        });
        return silent;
    }
}