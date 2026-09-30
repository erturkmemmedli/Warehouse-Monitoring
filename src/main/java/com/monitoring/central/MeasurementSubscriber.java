package com.monitoring.central;

import java.nio.charset.StandardCharsets;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.MqttGlobalPublishFilter;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish;
import com.monitoring.common.Measurement;
import com.monitoring.common.SensorType;

public class MeasurementSubscriber {

    private static final Logger log = LoggerFactory.getLogger(MeasurementSubscriber.class);
    private static final String TOPIC_FILTER = "warehouse/+/+/+";
    private static final String CONFIG_PREFIX = "config/threshold/";
    private final Mqtt5AsyncClient client;
    private final Consumer<Measurement> handler;
    private final BiConsumer<SensorType, Double> thresholdHandler;

    public MeasurementSubscriber(
        String host,
        int port,
        Consumer<Measurement> handler,
        BiConsumer<SensorType, Double> thresholdHandler
    ) {
        this.handler = handler;
        this.thresholdHandler = thresholdHandler;
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
        client.subscribeWith()
                .topicFilter(CONFIG_PREFIX + "+")
                .qos(MqttQos.AT_LEAST_ONCE)
                .send()
                .join();
        log.info("Subscribed to {} and {}+", TOPIC_FILTER, CONFIG_PREFIX);
    }

    private void onMessage(Mqtt5Publish publish) {
        String topic = publish.getTopic().toString();
        String payload = new String(publish.getPayloadAsBytes(), StandardCharsets.UTF_8);
        try {
            if (topic.startsWith(CONFIG_PREFIX)) {
                SensorType type = SensorType.valueOf(topic.substring(CONFIG_PREFIX.length()).toUpperCase());
                thresholdHandler.accept(type, Double.parseDouble(payload));
                return;
            }
            handler.accept(Measurement.fromMqtt(topic, payload));
        } catch (IllegalArgumentException e) {
            log.warn("Skipping message on {}: {}", topic, payload);
        }
    }
}