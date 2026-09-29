package com.monitoring.central;

import java.util.EnumMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.monitoring.common.Measurement;
import com.monitoring.common.SensorType;

public class ThresholdMonitor {

    private static final Logger log = LoggerFactory.getLogger(ThresholdMonitor.class);
    private final Map<SensorType, Limits> limitsMap;

    public ThresholdMonitor(Map<SensorType, Limits> limitsData) {
        this.limitsMap = new EnumMap<>(limitsData);
    }

    public boolean check(Measurement measurement) {
        Limits limits = limitsMap.get(measurement.type());
        if (!limits.isOutside(measurement.value())) {
            log.info("OK {}", measurement);
            return false;
        }
        log.warn(
            "ALARM! {} {} in {} is {} (limits {} to {})",
            measurement.type(),
            measurement.sensorId(),
            measurement.warehouseId(),
            measurement.value(),
            limits.min(),
            limits.max()
        );
        return true;
    }
}