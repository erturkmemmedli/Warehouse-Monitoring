package com.monitoring.central;

import java.io.IOException;
import java.util.Map;

import com.monitoring.common.Env;
import com.monitoring.common.SensorType;

public class CentralApp {

    public static void main(String[] args) throws InterruptedException, IOException {
        ThresholdMonitor monitor = new ThresholdMonitor(Map.of(
            SensorType.TEMPERATURE, Env.getDouble("TEMPERATURE_THRESHOLD", 35),
            SensorType.HUMIDITY, Env.getDouble("HUMIDITY_THRESHOLD", 50)
        ));

        StatusServer status = new StatusServer();
        status.start(Env.getInt("STATUS_PORT", 8080));

        new MeasurementSubscriber(
            Env.get("MQTT_HOST", "localhost"),
            Env.getInt("MQTT_PORT", 1883),
            measurement -> {
                status.record(measurement);
                monitor.check(measurement);
            }
        ).start();

        Thread.currentThread().join();
    }
}