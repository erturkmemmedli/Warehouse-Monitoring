package com.monitoring.central;

import java.time.Duration;
import java.util.List;
import java.util.function.Consumer;

import com.monitoring.common.Measurement;

import reactor.core.publisher.Sinks;

public class MeasurementAverager {

    private final Sinks.Many<Measurement> sink = Sinks.many().unicast().onBackpressureBuffer();

    public MeasurementAverager(Duration window, Consumer<Measurement> destination) {
        sink.asFlux()
                .groupBy(Measurement::topic)
                .flatMap(sensor -> sensor.window(window).flatMap(readings -> readings.collectList()))
                .filter(readings -> !readings.isEmpty())
                .map(MeasurementAverager::average)
                .subscribe(destination);
    }

    public void accept(Measurement measurement) {
        sink.emitNext(measurement, Sinks.EmitFailureHandler.busyLooping(Duration.ofSeconds(1)));
    }

    private static Measurement average(List<Measurement> readings) {
        Measurement first = readings.get(0);
        double average = readings.stream().mapToDouble(Measurement::value).average().orElseThrow();
        return new Measurement(first.warehouseId(), first.type(), first.sensorId(), average);
    }
}