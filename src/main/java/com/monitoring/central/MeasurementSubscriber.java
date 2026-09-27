package com.monitoring.central;

import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.MqttGlobalPublishFilter;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish;
import com.monitoring.common.Measurement;

public class MeasurementSubscriber {

    private static final Logger log = LoggerFactory.getLogger(MeasurementSubscriber.class);
    private static final String TOPIC_FILTER = "warehouse/+/+/+";
    private final Mqtt5AsyncClient client;
    private final Consumer<Measurement> handler;

    public MeasurementSubscriber(String host, int port, Consumer<Measurement> handler) {
        this.handler = handler;
        this.client = MqttClient.builder()
                .useMqttVersion5()
                .identifier("central-service")
                .serverHost(host)
                .serverPort(port)
                .automaticReconnectWithDefaultConfig()
                .buildAsync();
    }

    public void start() {
        client.publishes(MqttGlobalPublishFilter.ALL, this::onMessage);
        client.connectWith().cleanStart(false).noSessionExpiry().send().join();
        client.subscribeWith()
                .topicFilter(TOPIC_FILTER)
                .qos(MqttQos.AT_LEAST_ONCE)
                .send()
                .join();
        log.info("Subscribed to {}", TOPIC_FILTER);
    }

    private void onMessage(Mqtt5Publish publish) {
        String topic = publish.getTopic().toString();
        String payload = new String(publish.getPayloadAsBytes(), StandardCharsets.UTF_8);
        try {
            handler.accept(Measurement.fromMqtt(topic, payload));
        } catch (IllegalArgumentException e) {
            log.warn("Skipping message on {}: {}", topic, payload);
        }
    }
}