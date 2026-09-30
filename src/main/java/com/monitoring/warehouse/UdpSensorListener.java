package com.monitoring.warehouse;

import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.monitoring.common.Measurement;
import com.monitoring.common.SensorType;

import io.netty.channel.socket.DatagramPacket;
import reactor.netty.Connection;
import reactor.netty.udp.UdpServer;

public class UdpSensorListener {

    private static final Logger log = LoggerFactory.getLogger(UdpSensorListener.class);
    private final String warehouseId;
    private final Set<String> allowedHosts;
    private final Consumer<Measurement> destination;

    public UdpSensorListener(String warehouseId, Set<String> allowedHosts, Consumer<Measurement> destination) {
        this.warehouseId = warehouseId;
        this.allowedHosts = allowedHosts;
        this.destination = destination;
    }

    public Connection listen(int port, SensorType type) {
        return UdpServer.create()
                .host("0.0.0.0")
                .port(port)
                .handle((in, out) -> in.receiveObject()
                        .cast(DatagramPacket.class)
                        .filter(this::isTrusted)
                        .map(packet -> packet.content().toString(StandardCharsets.UTF_8))
                        .doOnNext(raw -> onMessage(raw, type))
                        .then())
                .bindNow();
    }

    private boolean isTrusted(DatagramPacket packet) {
        String host = packet.sender().getAddress().getHostAddress();
        if (allowedHosts.isEmpty() || allowedHosts.contains(host)) {
            return true;
        }
        log.warn("Ignoring datagram from untrusted host {}", host);
        return false;
    }

    private void onMessage(String raw, SensorType type) {
        MeasurementParser.parse(raw, warehouseId, type).ifPresentOrElse(
            destination,
            () -> log.warn("Invalid {} message: {}", type, raw.trim())
        );
    }
}