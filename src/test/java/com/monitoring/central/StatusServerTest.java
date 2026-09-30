package com.monitoring.central;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import com.monitoring.common.Measurement;
import com.monitoring.common.SensorType;

class StatusServerTest {

    @Test
    void returnsLatestValuePerSensor() throws Exception {
        StatusServer server = new StatusServer();
        server.record(new Measurement(
            "w1",
            SensorType.TEMPERATURE,
            "t1",
            30
        ));
        Measurement latest = new Measurement(
            "w1",
            SensorType.TEMPERATURE,
            "t1",
            31
        );
        server.record(latest);
        int port = server.start(0);

        HttpResponse<String> response = HttpClient.newHttpClient().send(
            HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/sensors")).build(),
            HttpResponse.BodyHandlers.ofString()
        );

        assertEquals(200, response.statusCode());
        assertEquals(latest.toString(), response.body());
    }
}