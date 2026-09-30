package com.monitoring.central;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.monitoring.common.Measurement;
import com.monitoring.common.SensorType;

public class ThresholdMonitor {

    private static final Logger log = LoggerFactory.getLogger(ThresholdMonitor.class);
    private final Map<SensorType, Double> thresholdMap;
    private final List<AlarmNotifier> notifiers;

    public ThresholdMonitor(Map<SensorType, Double> thresholdData, List<AlarmNotifier> notifiers) {
        this.thresholdMap = new EnumMap<>(thresholdData);
        this.notifiers = notifiers;
    }

    public boolean check(Measurement measurement) {
        double threshold = thresholdMap.get(measurement.type());
        if (measurement.value() <= threshold) {
            log.info("OK {}", measurement);
            return false;
        }
        Alarm alarm = new Alarm(measurement, threshold);
        notifiers.forEach(notifier -> notifier.notify(alarm));
        return true;
    }
}