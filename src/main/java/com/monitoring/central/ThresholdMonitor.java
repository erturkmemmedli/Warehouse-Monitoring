package com.monitoring.central;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.monitoring.common.Measurement;
import com.monitoring.common.SensorType;

public class ThresholdMonitor {

    private static final Logger log = LoggerFactory.getLogger(ThresholdMonitor.class);
    private final Map<SensorType, Double> thresholdMap;
    private final Set<String> activeAlarms = ConcurrentHashMap.newKeySet();
    private final Map<SensorType, Double> marginMap;

    public ThresholdMonitor(Map<SensorType, Double> thresholdData, Map<SensorType, Double> marginMap) {
        this.thresholdMap = new EnumMap<>(thresholdData);
        this.marginMap = new EnumMap<>(marginMap);
    }

    public boolean check(Measurement measurement) {
        String alarmKey = measurement.warehouseId() + "/" 
                            + measurement.type().toString() + "/" 
                                + measurement.sensorId();

        double threshold = thresholdMap.get(measurement.type());
        double margin = marginMap.get(measurement.type());

        if (measurement.value() > threshold) {
            if (activeAlarms.add(alarmKey)) {
                log.warn(
                    "ALARM! {} is {} (threshold {})",
                    alarmKey,
                    measurement.value(), 
                    threshold
                );
            }
            return true;
        }

        if (measurement.value() <= margin && activeAlarms.remove(alarmKey)) {
            log.info("CLEARED {} is back to {}", alarmKey, measurement.value());
        }
        return activeAlarms.contains(alarmKey);
    }
}