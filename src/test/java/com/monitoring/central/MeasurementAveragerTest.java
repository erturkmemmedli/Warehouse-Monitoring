package com.monitoring.central;

import java.time.Duration;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import com.monitoring.common.Measurement;
import com.monitoring.common.SensorType;

class MeasurementAveragerTest {

    private final BlockingQueue<Measurement> received = new LinkedBlockingQueue<>();
    private final MeasurementAverager averager = new MeasurementAverager(
        Duration.ofMillis(500),
        received::add
    );

    @Test
    void averagesEachSensorPerWindow() throws Exception {
        averager.accept(new Measurement("w1", SensorType.TEMPERATURE, "t1", 30));
        averager.accept(new Measurement("w1", SensorType.TEMPERATURE, "t1", 60));
        averager.accept(new Measurement("w1", SensorType.TEMPERATURE, "t2", 20));

        Set<Measurement> averages = Set.of(
            received.poll(5, TimeUnit.SECONDS),
            received.poll(5, TimeUnit.SECONDS)
        );

        assertEquals(Set.of(
            new Measurement("w1", SensorType.TEMPERATURE, "t1", 45),
            new Measurement("w1", SensorType.TEMPERATURE, "t2", 20)
        ), averages);
    }
}