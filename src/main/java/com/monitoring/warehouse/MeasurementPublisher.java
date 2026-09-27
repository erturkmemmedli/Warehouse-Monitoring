package com.monitoring.warehouse;

import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient;
import com.monitoring.common.Measurement;

public class MeasurementPublisher {

    private static final Logger log = LoggerFactory.getLogger(MeasurementPublisher.class);
    private final Mqtt5AsyncClient client;

    public MeasurementPublisher(String host, int port, String clientId) {
        client = MqttClient.builder()
                .useMqttVersion5()
                .identifier(clientId)
                .serverHost(host)
                .serverPort(port)
                .automaticReconnectWithDefaultConfig()
                .buildAsync();
        client.connect().join();
        log.info("Connected to broker {}:{}", host, port);
    }

    public void publish(Measurement measurement) {
        client.publishWith()
                .topic(measurement.topic())
                .payload(String.valueOf(measurement.value()).getBytes(StandardCharsets.UTF_8))
                .qos(MqttQos.AT_LEAST_ONCE)
                .send()
                .whenComplete((result, error) -> {
                    if (error != null) {
                        log.error("Failed to publish {}", measurement, error);
                    } else {
                        log.info("Published {}", measurement);
                    }
                });
    }
}