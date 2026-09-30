package com.monitoring.central;

import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient;

public class MqttAlarmNotifier implements AlarmNotifier {

    private static final Logger log = LoggerFactory.getLogger(MqttAlarmNotifier.class);
    private final Mqtt5AsyncClient client;

    public MqttAlarmNotifier(String host, int port) {
        client = MqttClient.builder()
                .useMqttVersion5()
                .identifier("central-alarms")
                .serverHost(host)
                .serverPort(port)
                .automaticReconnectWithDefaultConfig()
                .buildAsync();
        client.connect().join();
    }

    @Override
    public void notify(Alarm alarm) {
        String payload = "{\"value\":%s,\"threshold\":%s}".formatted(
            alarm.measurement().value(),
            alarm.threshold()
        );
        client.publishWith()
                .topic("alarms/" + alarm.measurement().topic())
                .payload(payload.getBytes(StandardCharsets.UTF_8))
                .qos(MqttQos.AT_LEAST_ONCE)
                .send()
                .whenComplete((result, error) -> {
                    if (error != null) {
                        log.error("Failed to publish {}", alarm, error);
                    }
                });
    }
}