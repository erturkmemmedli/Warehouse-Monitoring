package com.monitoring.warehouse;

import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.monitoring.common.Measurement;
import com.monitoring.common.SensorType;

import reactor.netty.Connection;
import reactor.netty.udp.UdpServer;

public class UdpSensorListener {

    private static final Logger log = LoggerFactory.getLogger(UdpSensorListener.class);
    private final String warehouseId;
    private final Consumer<Measurement> destination;

    public UdpSensorListener(String warehouseId, Consumer<Measurement> destination) {
        this.warehouseId = warehouseId;
        this.destination = destination;
    }

    public Connection listen(int port, SensorType type) {
        return UdpServer.create()
                .host("0.0.0.0")
                .port(port)
                .handle((in, out) -> in.receive()
                        .asString(StandardCharsets.UTF_8)
                        .doOnNext(raw -> onMessage(raw, type))
                        .then())
                .bindNow();
    }

    private void onMessage(String raw, SensorType type) {
        MeasurementParser.parse(raw, warehouseId, type).ifPresentOrElse(
            destination,
            () -> log.warn("Invalid {} message: {}", type, raw.trim())
        );
    }
}