package com.monitoring.central;

import java.util.EnumMap;
import java.util.Map;

import com.monitoring.common.Env;
import com.monitoring.common.SensorType;

public class CentralApp {

    public static void main(String[] args) throws InterruptedException {
        Map<SensorType, Double> thresholds = new EnumMap<>(SensorType.class);
        for (SensorType type : SensorType.values()) {
            thresholds.put(type, type.threshold());
        }

        ThresholdMonitor monitor = new ThresholdMonitor(thresholds);

        new MeasurementSubscriber(
            Env.get("MQTT_HOST", "localhost"), 
            Env.getInt("MQTT_PORT", 1883), 
            monitor::check
        ).start();

        Thread.currentThread().join();
    }
}