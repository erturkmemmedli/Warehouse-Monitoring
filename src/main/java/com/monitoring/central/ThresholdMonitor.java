package com.monitoring.central;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.monitoring.common.Measurement;
import com.monitoring.common.SensorType;

public class ThresholdMonitor {

    private static final Logger log = LoggerFactory.getLogger(ThresholdMonitor.class);
    private final Map<SensorType, Double> thresholdMap;
    private final Function<String, String> env;

    public ThresholdMonitor(Map<SensorType, Double> thresholdData, Function<String, String> env) {
        this.thresholdMap = new EnumMap<>(thresholdData);
        this.env = env;
    }

    public boolean check(Measurement measurement) {
        String override = env.apply(
            measurement.warehouseId().toUpperCase().replace('-', '_')
                + "_" + measurement.type() + "_THRESHOLD"
        );
        double threshold = override != null ? Double.parseDouble(override) : thresholdMap.get(measurement.type());
        if (measurement.value() <= threshold) {
            log.info("OK {}", measurement);
            return false;
        }
        log.warn(
            "ALARM! {} {} in {} is {} (threshold {})",
            measurement.type(), 
            measurement.sensorId(), 
            measurement.warehouseId(),
            measurement.value(), 
            threshold
        );
        return true;
    }
}