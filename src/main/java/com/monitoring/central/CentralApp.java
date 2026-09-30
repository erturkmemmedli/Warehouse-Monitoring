package com.monitoring.central;

import java.time.Duration;
import java.util.Map;

import com.monitoring.common.Env;
import com.monitoring.common.SensorType;

public class CentralApp {

    public static void main(String[] args) throws InterruptedException {
        ThresholdMonitor monitor = new ThresholdMonitor(Map.of(
            SensorType.TEMPERATURE, Env.getDouble("TEMPERATURE_THRESHOLD", 35),
            SensorType.HUMIDITY, Env.getDouble("HUMIDITY_THRESHOLD", 50)
        ));

        SensorWatchdog watchdog = new SensorWatchdog(
            Duration.ofSeconds(Env.getInt("SENSOR_TIMEOUT_SECONDS", 60))
        );
        watchdog.start();

        new MeasurementSubscriber(
            Env.get("MQTT_HOST", "localhost"),
            Env.getInt("MQTT_PORT", 1883),
            measurement -> {
                watchdog.record(measurement);
                monitor.check(measurement);
            }
        ).start();

        Thread.currentThread().join();
    }
}