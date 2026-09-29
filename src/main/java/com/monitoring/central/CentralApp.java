package com.monitoring.central;

import java.util.Map;

import com.monitoring.common.Env;
import com.monitoring.common.SensorType;

public class CentralApp {

    public static void main(String[] args) throws InterruptedException {
        ThresholdMonitor monitor = new ThresholdMonitor(Map.of(
            SensorType.TEMPERATURE, new Limits(
                Env.getDouble("TEMPERATURE_MIN", 10),
                Env.getDouble("TEMPERATURE_THRESHOLD", 35)
            ),
            SensorType.HUMIDITY, new Limits(
                Env.getDouble("HUMIDITY_MIN", 10),
                Env.getDouble("HUMIDITY_THRESHOLD", 50)
            )
        ));

        new MeasurementSubscriber(
            Env.get("MQTT_HOST", "localhost"), 
            Env.getInt("MQTT_PORT", 1883), 
            monitor::check
        ).start();

        Thread.currentThread().join();
    }
}