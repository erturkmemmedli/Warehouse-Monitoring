package com.monitoring.central;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.monitoring.common.Measurement;
import com.sun.net.httpserver.HttpServer;

public class StatusServer {

    private final Map<String, Measurement> latest = new ConcurrentHashMap<>();

    public void record(Measurement measurement) {
        latest.put(measurement.topic(), measurement);
    }

    public int start(int port) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/sensors", exchange -> {
            byte[] body = latest.values().stream()
                .map(Measurement::toString)
                .collect(Collectors.joining("\n"))
                .getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, body.length);
            try (OutputStream out = exchange.getResponseBody()) {
                out.write(body);
            }
        });
        server.start();
        return server.getAddress().getPort();
    }
}