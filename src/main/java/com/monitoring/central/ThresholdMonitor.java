package com.monitoring.central;

import java.util.EnumMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.monitoring.common.Measurement;
import com.monitoring.common.SensorType;

public class ThresholdMonitor {

    private static final Logger log = LoggerFactory.getLogger(ThresholdMonitor.class);
    private final Map<SensorType, Double> thresholdMap;

    public ThresholdMonitor(Map<SensorType, Double> thresholdData) {
        this.thresholdMap = new EnumMap<>(thresholdData);
    }

    public boolean check(Measurement measurement) {
        double threshold = thresholdMap.get(measurement.type());
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