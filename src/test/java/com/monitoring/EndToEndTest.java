package com.monitoring;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.MountableFile;

import com.monitoring.central.MeasurementSubscriber;
import com.monitoring.common.Measurement;
import com.monitoring.common.SensorType;
import com.monitoring.warehouse.MeasurementPublisher;

@Testcontainers(disabledWithoutDocker = true)
class EndToEndTest {

    @Container
    static GenericContainer<?> mosquitto = new GenericContainer<>("eclipse-mosquitto:2")
        .withExposedPorts(1883)
        .withCopyFileToContainer(
            MountableFile.forHostPath("mosquitto/mosquitto.conf"),
            "/mosquitto/config/mosquitto.conf"
        );

    @Test
    void measurementReachesCentral() throws Exception {
        String host = mosquitto.getHost();
        int port = mosquitto.getMappedPort(1883);
        BlockingQueue<Measurement> received = new LinkedBlockingQueue<>();

        new MeasurementSubscriber(host, port, received::add).start();
        Measurement measurement = new Measurement(
            "w1",
            SensorType.TEMPERATURE,
            "t1",
            40
        );
        new MeasurementPublisher(host, port, "test-warehouse").publish(measurement);

        assertEquals(measurement, received.poll(5, TimeUnit.SECONDS));
    }
}