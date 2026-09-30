package com.monitoring.central;

import java.util.List;
import java.util.Map;

import com.monitoring.common.Env;
import com.monitoring.common.SensorType;

public class CentralApp {

    public static void main(String[] args) throws InterruptedException {
        String mqttHost = Env.get("MQTT_HOST", "localhost");
        int mqttPort = Env.getInt("MQTT_PORT", 1883);

        ThresholdMonitor monitor = new ThresholdMonitor(
            Map.of(
                SensorType.TEMPERATURE, Env.getDouble("TEMPERATURE_THRESHOLD", 35),
                SensorType.HUMIDITY, Env.getDouble("HUMIDITY_THRESHOLD", 50)
            ),
            List.of(
                new LogAlarmNotifier(),
                new MqttAlarmNotifier(mqttHost, mqttPort)
            )
        );

        new MeasurementSubscriber(
            mqttHost,
            mqttPort,
            monitor::check
        ).start();

        Thread.currentThread().join();
    }
}