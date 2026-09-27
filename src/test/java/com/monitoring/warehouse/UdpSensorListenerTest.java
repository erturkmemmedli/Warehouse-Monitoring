package com.monitoring.warehouse;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import com.monitoring.common.Measurement;
import com.monitoring.common.SensorType;

import reactor.netty.Connection;

class UdpSensorListenerTest {

    private final BlockingQueue<Measurement> received = new LinkedBlockingQueue<>();
    private final Connection connection = new UdpSensorListener(
        "w1",
        received::add
    ).listen(0, SensorType.TEMPERATURE);

    @AfterEach
    void stop() {
        connection.disposeNow();
    }

    @Test
    void forwardsOnlyValidDatagrams() throws Exception {
        send("garbage");
        send("sensor_id=t1; value=36");
        Measurement experimentedMeasurement = received.poll(
            5, 
            TimeUnit.SECONDS
        );
        Measurement createdMeasurement = new Measurement(
            "w1", 
            SensorType.TEMPERATURE, 
            "t1", 
            36
        );
        assertEquals(createdMeasurement, experimentedMeasurement);
        assertEquals(0, received.size());
    }

    private void send(String message) throws Exception {
        byte[] data = message.getBytes(StandardCharsets.UTF_8);
        InetSocketAddress address = (InetSocketAddress) connection.address();
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.send(new DatagramPacket(
                data, 
                data.length, 
                new InetSocketAddress("127.0.0.1", address.getPort())
            ));
        }
    }
}