package com.monitoring.warehouse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.monitoring.common.Env;
import com.monitoring.common.SensorType;

import reactor.netty.Connection;

public class WarehouseApp {

    private static final Logger log = LoggerFactory.getLogger(WarehouseApp.class);

    public static void main(String[] args) throws InterruptedException {
        String warehouseId = Env.get("WAREHOUSE_ID", "warehouse-1");
        int temperaturePort = Env.getInt("TEMPERATURE_PORT", 3344);
        int humidityPort = Env.getInt("HUMIDITY_PORT", 3355);

        MeasurementPublisher publisher = new MeasurementPublisher(
            Env.get("MQTT_HOST", "localhost"), 
            Env.getInt("MQTT_PORT", 1883), 
            warehouseId
        );

        UdpSensorListener listener = new UdpSensorListener(warehouseId, publisher::publish);

        Connection temperature = listener.listen(temperaturePort, SensorType.TEMPERATURE);
        Connection humidity = listener.listen(humidityPort, SensorType.HUMIDITY);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Shutting down warehouse {}", warehouseId);
            temperature.disposeNow();
            humidity.disposeNow();
            publisher.close();
        }));

        log.info(
            "Warehouse {} listening on UDP {} and {}", 
            warehouseId, temperaturePort, humidityPort
        );

        Thread.currentThread().join();
    }
}